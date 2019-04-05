package ru.inovus.messaging.impl;

import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inovus.messaging.api.UnreadMessagesInfo;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.*;
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
import static ru.inovus.messaging.impl.jooq.Sequences.*;
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
        List<InfoType> infoTypes = new ArrayList<>();
        if (record.getSendNotice() != null && record.getSendNotice()) {
            infoTypes.add(InfoType.NOTICE);
        }
        if (record.getSendEmail() != null && record.getSendEmail()) {
            infoTypes.add(InfoType.EMAIL);
        }
        message.setInfoTypes(infoTypes);
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
        Long id = dsl.nextval(MESSAGE_ID_SEQ);
        dsl
                .insertInto(MESSAGE)
                .columns(MESSAGE.ID, MESSAGE.CAPTION, MESSAGE.TEXT, MESSAGE.SEVERITY, MESSAGE.ALERT_TYPE,
                        MESSAGE.SENT_AT, MESSAGE.SYSTEM_ID, MESSAGE.COMPONENT_ID, MESSAGE.FORMATION_TYPE,
                        MESSAGE.RECIPIENT_TYPE, MESSAGE.NOTIFICATION_TYPE, MESSAGE.OBJECT_ID, MESSAGE.OBJECT_TYPE,
                        MESSAGE.SEND_NOTICE, MESSAGE.SEND_EMAIL)
                .values(id.toString(),
                        message.getCaption(),
                        message.getText(),
                        message.getSeverity(),
                        message.getAlertType(),
                        LocalDateTime.now(),
                        message.getSystemId(),
                        message.getComponent() != null ? message.getComponent().getId() : null,
                        message.getFormationType(),
                        message.getRecipientType(),
                        message.getNotificationType(),
                        message.getObjectId(),
                        message.getObjectType(),
                        message.getInfoTypes() != null && message.getInfoTypes().contains(InfoType.NOTICE),
                        message.getInfoTypes() != null && message.getInfoTypes().contains(InfoType.EMAIL))
                .returning()
                .fetch().get(0).getId();
        message.setId(id.toString());
        if (recipient != null && RecipientType.USER.equals(message.getRecipientType())) {
            for (Recipient rec : recipient) {
                dsl
                        .insertInto(RECIPIENT)
                        .values(RECIPIENT_ID_SEQ.nextval(),
                                rec.getRecipient(), id, null, rec.getEmail())
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
        if (InfoType.EMAIL.equals(criteria.getInfoType())) {
            Optional.ofNullable(criteria.getInfoType())
                    .ifPresent(infoType -> conditions.add(MESSAGE.SEND_EMAIL.isTrue()));
        }
        if (InfoType.NOTICE.equals(criteria.getInfoType())) {
            Optional.ofNullable(criteria.getInfoType())
                    .ifPresent(infoType -> conditions.add(MESSAGE.SEND_NOTICE.isTrue()));
        }
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
                    recipient.setEmail(r.getEmail());
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
                    field.asc() : field.desc();
            querySortFields.add(querySortField);
        }

        return querySortFields;
    }

}
