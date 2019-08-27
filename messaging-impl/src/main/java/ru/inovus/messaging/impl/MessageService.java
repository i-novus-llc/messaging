package ru.inovus.messaging.impl;

import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.*;
import ru.inovus.messaging.impl.jooq.tables.records.ComponentRecord;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                MESSAGE.SENT_AT, MESSAGE.SYSTEM_ID, MESSAGE.COMPONENT_ID,
                MESSAGE.FORMATION_TYPE, MESSAGE.RECIPIENT_TYPE, MESSAGE.NOTIFICATION_TYPE, MESSAGE.OBJECT_ID,
                MESSAGE.OBJECT_TYPE, MESSAGE.SEND_NOTICE,
                MESSAGE.SEND_EMAIL)
            .values(id.toString(), message.getCaption(), message.getText(), message.getSeverity(), message.getAlertType(),
                LocalDateTime.now(Clock.systemUTC()), message.getSystemId(), message.getComponent() != null ?
                    message.getComponent().getId() : null,
                message.getFormationType(), message.getRecipientType(), message.getNotificationType(), message.getObjectId(),
                message.getObjectType(), message.getInfoTypes() != null && message.getInfoTypes().contains(InfoType.NOTICE),
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


    public Page<Message> getMessages(MessageCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
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
        Field fieldSentAt = MESSAGE.field("sent_at");
        List<Message> collection = query
            .orderBy(fieldSentAt.desc())
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

}
