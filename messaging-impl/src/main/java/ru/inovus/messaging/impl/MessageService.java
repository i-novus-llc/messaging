package ru.inovus.messaging.impl;

import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inovus.messaging.api.UnreadMessagesInfo;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.Component;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.RecipientType;
import ru.inovus.messaging.impl.jooq.tables.records.ComponentRecord;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.notExists;
import static ru.inovus.messaging.impl.jooq.Sequences.MESSAGE_ID_SEQ;
import static ru.inovus.messaging.impl.jooq.Sequences.RECIPIENT_ID_SEQ;
import static ru.inovus.messaging.impl.jooq.Tables.*;

@Service
public class MessageService {
    private static final RecordMapper<Record, Message> MAPPER = rec -> {
        MessageRecord record = rec.into(MESSAGE);
        ComponentRecord componentRecord = rec.into(COMPONENT);
        Message message = new Message();
        message.setId(String.valueOf(record.getId()));
        message.setCaption(record.getCaption());
        message.setText(record.getText());
        message.setAlertType(record.getAlertType());
        message.setSeverity(record.getSeverity());
        message.setSentAt(record.getSentAt());
        message.setInfoType(record.getInfoType());
        message.setFormationType(record.getFormationType());
        message.setRecipientType(record.getRecipientType());
        message.setSystemId(record.getSystemId());
        if (componentRecord != null) {
            message.setComponent(new Component(componentRecord.getId(), componentRecord.getName()));
        }
        return message;
    };
    private final DSLContext dsl;

    public MessageService(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public Message createMessage(Message message, Recipient... recipient) {
        String id = dsl
                .insertInto(MESSAGE)
                .values(message.getId() != null ? message.getId() : MESSAGE_ID_SEQ.nextval(),
                        message.getCaption(),
                        message.getText(),
                        message.getSeverity(),
                        message.getAlertType(),
                        LocalDateTime.now(),
                        message.getSystemId(),
                        message.getInfoType(),
                        message.getComponent() != null ? message.getComponent().getId() : null,
                        message.getFormationType(),
                        message.getRecipientType())
                .returning()
                .fetch().get(0).getId();
        message.setId(String.valueOf(id));
        if (recipient != null && RecipientType.USER.equals(message.getRecipientType())) {
            for (Recipient rec : recipient) {
                dsl
                        .insertInto(RECIPIENT)
                        .values(RECIPIENT_ID_SEQ.nextval(),
                                rec.getRecipient(), id, null, rec.getUserId())
                        .execute();
            }
        }
        return message;
    }

    public UnreadMessagesInfo getUnreadMessages(String recipient, String systemId) {
        Integer count = dsl
                .selectCount()
                .from(MESSAGE)
                .where(notExists(dsl.selectOne()
                                .from(RECIPIENT)
                                .where(RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID),
                                        RECIPIENT.READ_AT.isNotNull())),
                        MESSAGE.SYSTEM_ID.eq(systemId))
                .fetchOne().value1();
        return new UnreadMessagesInfo(count);
    }

    @Transactional
    public void markRead(String recipient, String... messageId) {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        int updated = dsl
                .update(RECIPIENT)
                .set(RECIPIENT.READ_AT, now)
                .where(RECIPIENT.MESSAGE_ID.in(messageId))
                .execute();
        if (updated == 0 && messageId != null) {
            for (String id : messageId) {
                dsl
                        .insertInto(RECIPIENT)
                        .set(RECIPIENT.READ_AT, now)
                        .set(RECIPIENT.MESSAGE_ID, id)
                        .set(RECIPIENT.RECIPIENT_, recipient)
                        .execute();
            }
        }
    }

    private void setSentTime(String systemId, LocalDateTime sentAt, String... messageId) {
        dsl
                .update(MESSAGE)
                .set(MESSAGE.SENT_AT, sentAt)
                .where(MESSAGE.ID.in(messageId),
                        MESSAGE.SYSTEM_ID.eq(systemId))
                .execute();
    }

    public void setSentTime(String systemId, String... messageId) {
        setSentTime(systemId, LocalDateTime.now(Clock.systemUTC()), messageId);
    }

    @Transactional
    public void markReadAll(String recipient, String systemId) {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        // Update 'personal' messages
        dsl
                .update(RECIPIENT)
                .set(RECIPIENT.READ_AT, now)
                .where(RECIPIENT.RECIPIENT_.eq(recipient),
                        exists(dsl.selectOne().from(MESSAGE)
                                .where(MESSAGE.ID.eq(RECIPIENT.MESSAGE_ID),
                                        MESSAGE.SYSTEM_ID.eq(systemId))))
                .execute();
        // Update messages 'for all'
        List<String> ids = dsl
                .select(MESSAGE.ID)
                .from(MESSAGE)
                .where(MESSAGE.RECIPIENT_TYPE.eq(RecipientType.ALL),
                        MESSAGE.SYSTEM_ID.eq(systemId),
                        notExists(dsl.selectOne().from(RECIPIENT)
                                .where(RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID),
                                        RECIPIENT.RECIPIENT_.eq(recipient))))
                .fetch().map(Record1::component1);
        for (String id : ids) {
            dsl
                    .insertInto(RECIPIENT)
                    .set(RECIPIENT.READ_AT, now)
                    .set(RECIPIENT.MESSAGE_ID, id)
                    .set(RECIPIENT.RECIPIENT_, recipient)
                    .execute();
        }
    }

    public Page<Message> getMessages(MessageCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        Optional.ofNullable(criteria.getUser())
                .ifPresent(user -> conditions.add(exists(dsl.selectOne().from(RECIPIENT)
                        .where(RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID),
                                RECIPIENT.RECIPIENT_.eq(user))).or(MESSAGE.RECIPIENT_TYPE.eq(RecipientType.ALL))));
        //TODO: Recipient type
        Optional.ofNullable(criteria.getSystemId())
                .ifPresent(systemId -> conditions.add(MESSAGE.SYSTEM_ID.eq(systemId)));
        Optional.ofNullable(criteria.getComponentId())
                .ifPresent(componentId -> conditions.add(MESSAGE.COMPONENT_ID.eq(componentId)));
        Optional.ofNullable(criteria.getSeverity())
                .ifPresent(severity -> conditions.add(MESSAGE.SEVERITY.eq(severity)));
        Optional.ofNullable(criteria.getInfoType())
                .ifPresent(infoType -> conditions.add(MESSAGE.INFO_TYPE.eq(infoType)));
        //TODO: UTC?
        Optional.ofNullable(criteria.getSentAtBegin())
                .ifPresent(start -> conditions.add(MESSAGE.SENT_AT.greaterOrEqual(start)));
        Optional.ofNullable(criteria.getSentAtEnd())
                .ifPresent(end -> conditions.add(MESSAGE.SENT_AT.lessOrEqual(end)));
        SelectConditionStep<Record> query = dsl
                .select(MESSAGE.fields())
                .select(COMPONENT.fields())
                .from(MESSAGE)
                .leftJoin(COMPONENT).on(COMPONENT.ID.eq(MESSAGE.COMPONENT_ID))
                .where(conditions);
        int count = dsl.fetchCount(query);
        List<Message> collection = query
                .orderBy(getSortFields(criteria.getOrders()))
                .limit(criteria.getPageSize())
                .offset((int) criteria.getOffset())
                .fetch(MAPPER);
        return new PageImpl<>(collection, criteria, count);
    }

    public Message getMessage(String messageId) {
        Message message = dsl
                .select(MESSAGE.fields())
                .select(COMPONENT.fields())
                .from(MESSAGE)
                .leftJoin(COMPONENT).on(COMPONENT.ID.eq(MESSAGE.COMPONENT_ID))
                .where(MESSAGE.ID.cast(String.class).eq(messageId))
                .fetchOne(MAPPER);
        List<Recipient> recipients = dsl
                .selectFrom(RECIPIENT)
                .where(RECIPIENT.MESSAGE_ID.eq(messageId))
                .fetch().map(r -> {
                    Recipient recipient = new Recipient();
                    recipient.setId(r.getId());
                    recipient.setMessageId(r.getMessageId());
                    recipient.setReadAt(r.getReadAt());
                    recipient.setRecipient(r.getRecipient());
                    recipient.setUserId(r.getUserId());
                    return recipient;
                });
        message.setRecipients(recipients);
        return message;
    }

    private Collection<SortField<?>> getSortFields(List<Sort.Order> sortingList) {
        Collection<SortField<?>> querySortFields = new ArrayList<>();

        if (sortingList == null) {
            return querySortFields;
        }

        for (Sort.Order sorting : sortingList) {
            Field field = MESSAGE.field(sorting.getProperty());
            SortField<?> querySortField = sorting.getDirection().equals(Sort.Direction.ASC) ?
                    field.asc(): field.desc();
            querySortFields.add(querySortField);
        }

        return querySortFields;
    }

}
