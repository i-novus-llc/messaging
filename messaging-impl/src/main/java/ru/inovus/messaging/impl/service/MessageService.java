package ru.inovus.messaging.impl.service;

import org.jooq.Record;
import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.Channel;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.impl.jooq.tables.records.ChannelRecord;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;
import ru.inovus.messaging.impl.util.DateTimeUtil;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.inovus.messaging.impl.jooq.Sequences.RECIPIENT_ID_SEQ;
import static ru.inovus.messaging.impl.jooq.Tables.*;

@Service
public class MessageService {

    private static final ZoneId USER_DEFAULT_ZONE_ID = ZoneId.of("Europe/Moscow");

    private static final RecordMapper<Record, Message> MAPPER = rec -> {
        MessageRecord record = rec.into(MESSAGE);
        Message message = new Message();
        message.setId(String.valueOf(record.getId()));
        message.setCaption(record.getCaption());
        message.setText(record.getText());
        message.setAlertType(record.getAlertType());
        message.setSeverity(record.getSeverity());
        message.setSentAt(record.getSentAt());
        ChannelRecord channelRecord = rec.into(CHANNEL);
        Channel channel = new Channel();
        channel.setId(channelRecord.getId());
        channel.setName(channelRecord.getName());
        channel.setQueueName(channelRecord.getQueueName());
        message.setChannel(channel);
        message.setFormationType(record.getFormationType());
        message.setRecipientType(record.getRecipientType());
        message.setTenantCode(record.getTenantCode());
        return message;
    };
    private final DSLContext dsl;

    public MessageService(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public Message createMessage(Message message, Recipient... recipients) {
        UUID id = UUID.randomUUID();
        if (message.getSentAt() == null) {
            message.setSentAt(LocalDateTime.now(Clock.systemUTC()));
        }
        dsl
                .insertInto(MESSAGE)
                .columns(MESSAGE.ID, MESSAGE.CAPTION, MESSAGE.TEXT, MESSAGE.SEVERITY, MESSAGE.ALERT_TYPE,
                        MESSAGE.SENT_AT, MESSAGE.TENANT_CODE,
                        MESSAGE.FORMATION_TYPE, MESSAGE.RECIPIENT_TYPE, MESSAGE.NOTIFICATION_TYPE, MESSAGE.CHANNEL_ID)
                .values(id, message.getCaption(), message.getText(), message.getSeverity(), message.getAlertType(),
                        message.getSentAt(), message.getTenantCode(),
                        message.getFormationType(), message.getRecipientType(), message.getNotificationType(),
                        message.getChannel() != null ? message.getChannel().getId() : null)
                .returning()
                .fetch().get(0).getId();
        message.setId(id.toString());
        if (recipients != null && RecipientType.RECIPIENT.equals(message.getRecipientType())) {
            for (Recipient recipient : recipients) {
                dsl
                        .insertInto(MESSAGE_RECIPIENT)
                        .columns(MESSAGE_RECIPIENT.ID, MESSAGE_RECIPIENT.RECIPIENT_NAME, MESSAGE_RECIPIENT.MESSAGE_ID,
                                MESSAGE_RECIPIENT.STATUS, MESSAGE_RECIPIENT.STATUS_TIME, MESSAGE_RECIPIENT.RECIPIENT_USERNAME)
                        .values(dsl.nextval(RECIPIENT_ID_SEQ).intValue(), recipient.getName(), id, MessageStatusType.SCHEDULED, LocalDateTime.now(), recipient.getUsername())
                        .execute();
            }
        }
        return message;
    }

    public Page<Message> getMessages(String tenantCode, MessageCriteria criteria) {
        LocalDateTime sentAtBeginDateTime = null;
        LocalDateTime sentAtEndDateTime = null;

        if (criteria.getSentAtBegin() != null) {
            sentAtBeginDateTime = DateTimeUtil.toZone(criteria.getSentAtBegin(), USER_DEFAULT_ZONE_ID, ZoneOffset.UTC);
        }
        if (criteria.getSentAtEnd() != null) {
            sentAtEndDateTime = DateTimeUtil.toZone(criteria.getSentAtEnd(), USER_DEFAULT_ZONE_ID, ZoneOffset.UTC);
        }

        List<Condition> conditions = new ArrayList<>();
        conditions.add(MESSAGE.TENANT_CODE.eq(tenantCode));
        Optional.ofNullable(criteria.getSeverity())
                .ifPresent(severity -> conditions.add(MESSAGE.SEVERITY.eq(severity)));
        Optional.ofNullable(criteria.getChannelId())
                .ifPresent(channelId -> conditions.add(MESSAGE.CHANNEL_ID.eq(channelId)));

        //TODO: UTC?
        Optional.ofNullable(sentAtBeginDateTime)
                .ifPresent(start -> conditions.add(MESSAGE.SENT_AT.greaterOrEqual(start)));
        Optional.ofNullable(sentAtEndDateTime)
                .ifPresent(end -> conditions.add(MESSAGE.SENT_AT.lessOrEqual(end)));
        SelectConditionStep<Record> query = dsl
                .select(MESSAGE.fields())
                .select(CHANNEL.fields())
                .from(MESSAGE)
                .leftJoin(CHANNEL).on(CHANNEL.ID.eq(MESSAGE.CHANNEL_ID))
                .where(conditions);
        int count = dsl.fetchCount(query);
        Field<?> fieldSentAt = MESSAGE.field("sent_at");
        List<Message> collection = query
                .orderBy(fieldSentAt.desc())
                .limit(criteria.getPageSize())
                .offset((int) criteria.getOffset())
                .fetch(MAPPER);
        return new PageImpl<>(collection, criteria, count);
    }

    public Message getMessage(UUID messageId) {
        Message message = dsl
                .select(MESSAGE.fields())
                .select(CHANNEL.fields())
                .from(MESSAGE)
                .leftJoin(CHANNEL).on(CHANNEL.ID.eq(MESSAGE.CHANNEL_ID))
                .where(MESSAGE.ID.cast(UUID.class).eq(messageId))
                .fetchOne(MAPPER);
        List<Recipient> recipients = dsl
                .selectFrom(MESSAGE_RECIPIENT)
                .where(MESSAGE_RECIPIENT.MESSAGE_ID.eq(messageId))
                .fetch().map(r -> {
                    Recipient recipient = new Recipient();
                    recipient.setMessageId(r.getMessageId());
                    recipient.setStatusTime(r.getStatusTime());
                    recipient.setName(r.getRecipientName());
                    recipient.setUsername(r.getRecipientUsername());
                    return recipient;
                });
        message.setRecipients(recipients);
        return message;
    }
}
