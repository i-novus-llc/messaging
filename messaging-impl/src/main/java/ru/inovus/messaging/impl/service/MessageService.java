package ru.inovus.messaging.impl.service;

import org.jooq.Record;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.inovus.messaging.impl.jooq.Sequences.MESSAGE_RECIPIENT_ID_SEQ;
import static ru.inovus.messaging.impl.jooq.Tables.*;

/**
 * Сервис уведомлений
 */
@Service
public class MessageService {

    @Autowired
    private DSLContext dsl;

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
        channel.setId(channelRecord.getCode());
        channel.setName(channelRecord.getName());
        channel.setQueueName(channelRecord.getQueueName());
        message.setChannel(channel);
        message.setRecipientType(record.getRecipientType());
        message.setTenantCode(record.getTenantCode());
        message.setTemplateCode(record.getTemplateCode());
        return message;
    };


    /**
     * Создание уведомления
     *
     * @param message    Уведомление
     * @param recipients Список получателей
     * @return Созданное уведомление
     */
    @Transactional
    public Message createMessage(Message message, Recipient... recipients) {
        UUID id = UUID.randomUUID();
        if (message.getSentAt() == null)
            message.setSentAt(LocalDateTime.now(Clock.systemUTC()));

        dsl
                .insertInto(MESSAGE)
                .columns(MESSAGE.ID, MESSAGE.CAPTION, MESSAGE.TEXT, MESSAGE.SEVERITY, MESSAGE.ALERT_TYPE,
                        MESSAGE.SENT_AT, MESSAGE.TENANT_CODE,
                        MESSAGE.RECIPIENT_TYPE, MESSAGE.TEMPLATE_CODE, MESSAGE.CHANNEL_CODE)
                .values(id, message.getCaption(), message.getText(), message.getSeverity(), message.getAlertType(),
                        message.getSentAt(), message.getTenantCode(),
                        message.getRecipientType(), message.getTemplateCode(),
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
                        .values(dsl.nextval(MESSAGE_RECIPIENT_ID_SEQ), recipient.getName(), id,
                                MessageStatusType.SCHEDULED, LocalDateTime.now(), recipient.getUsername())
                        .execute();
            }
        }
        return message;
    }

    /**
     * Получение страницы уведомлений
     *
     * @param tenantCode Код тенанта
     * @param criteria   Критерии уведомлений
     * @return Страница уведомлений
     */
    public Page<Message> getMessages(String tenantCode, MessageCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(MESSAGE.TENANT_CODE.eq(tenantCode));
        Optional.ofNullable(criteria.getSeverity())
                .ifPresent(severity -> conditions.add(MESSAGE.SEVERITY.eq(severity)));
        Optional.ofNullable(criteria.getChannelCode())
                .ifPresent(channelCode -> conditions.add(MESSAGE.CHANNEL_CODE.eq(channelCode)));
        Optional.ofNullable(criteria.getSentAtBegin())
                .ifPresent(start -> conditions.add(MESSAGE.SENT_AT.greaterOrEqual(start)));
        Optional.ofNullable(criteria.getSentAtEnd())
                .ifPresent(end -> conditions.add(MESSAGE.SENT_AT.lessOrEqual(end)));

        SelectConditionStep<Record> query = dsl
                .select(MESSAGE.fields())
                .select(CHANNEL.fields())
                .from(MESSAGE)
                .leftJoin(CHANNEL).on(CHANNEL.CODE.eq(MESSAGE.CHANNEL_CODE))
                .where(conditions);
        int count = dsl.fetchCount(query);
        List<Message> collection = query
                .orderBy(MESSAGE.SENT_AT.desc())
                .limit(criteria.getPageSize())
                .offset((int) criteria.getOffset())
                .fetch(MAPPER);
        return new PageImpl<>(collection, criteria, count);
    }

    /**
     * Получение уведомления по его идентификатору
     *
     * @param messageId Идентификатор уведомления
     * @return Уведомление
     */
    public Message getMessage(UUID messageId) {
        Message message = dsl
                .select(MESSAGE.fields())
                .select(CHANNEL.fields())
                .from(MESSAGE)
                .leftJoin(CHANNEL).on(CHANNEL.CODE.eq(MESSAGE.CHANNEL_CODE))
                .where(MESSAGE.ID.cast(UUID.class).eq(messageId))
                .fetchOne(MAPPER);
        List<Recipient> recipients = dsl
                .selectFrom(MESSAGE_RECIPIENT)
                .where(MESSAGE_RECIPIENT.MESSAGE_ID.eq(messageId))
                .fetch().map(r -> {
                    Recipient recipient = new Recipient();
                    recipient.setId(r.getId());
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
