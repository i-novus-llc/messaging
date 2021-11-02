package ru.inovus.messaging.impl.service;

import org.jooq.Record;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inovus.messaging.api.criteria.FeedCriteria;
import ru.inovus.messaging.api.model.Feed;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecipientRecord;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.jooq.impl.DSL.exists;
import static ru.inovus.messaging.impl.jooq.Tables.*;

/**
 * Сервис уведомлений пользователя
 */
@Service
public class FeedService {

    private static final RecordMapper<Record, Feed> MAPPER = rec -> {
        MessageRecord record = rec.into(MESSAGE);
        MessageRecipientRecord recipientRecord = rec.into(MESSAGE_RECIPIENT);
        Feed message = new Feed();
        message.setId(String.valueOf(record.getId()));
        message.setCaption(record.getCaption());
        message.setText(record.getText());
        message.setSeverity(record.getSeverity());
        message.setSentAt(record.getSentAt());
        if (MessageStatusType.READ.equals(recipientRecord.getStatus()))
            message.setReadAt(recipientRecord.getStatusTime());
        return message;
    };

    @Autowired
    private DSLContext dsl;

    /**
     * Получение страницы уведомлений пользователя
     *
     * @param tenantCode Код тенанта
     * @param username   Имя пользователя
     * @param criteria   Критерии уведомлений
     * @return Страница уведомлений пользователя
     */
    public Page<Feed> getMessageFeed(String tenantCode, String username, FeedCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(CHANNEL.IS_INTERNAL.eq(Boolean.TRUE));
        conditions.add(MESSAGE.TENANT_CODE.eq(tenantCode));
        Optional.ofNullable(criteria.getSeverity())
                .ifPresent(severity -> conditions.add(MESSAGE.SEVERITY.eq(severity)));
        Optional.ofNullable(criteria.getSentAtBegin())
                .ifPresent(start -> conditions.add(MESSAGE.SENT_AT.greaterOrEqual(start)));
        Optional.ofNullable(criteria.getSentAtEnd())
                .ifPresent(end -> conditions.add(MESSAGE.SENT_AT.lessOrEqual(end)));

        SelectConditionStep<Record> query = dsl
                .select(MESSAGE.fields())
                .select(MESSAGE_RECIPIENT.fields())
                .from(MESSAGE)
                .leftJoin(MESSAGE_RECIPIENT).on(MESSAGE_RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID)
                        .and(MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(username)))
                .leftJoin(CHANNEL).on(CHANNEL.CODE.eq(MESSAGE.CHANNEL_CODE))
                .where(conditions);
        int count = dsl.fetchCount(query);

        List<Feed> collection = query
                .orderBy(MESSAGE.SENT_AT.desc())
                .limit(criteria.getPageSize())
                .offset((int) criteria.getOffset())
                .fetch(MAPPER);
        return new PageImpl<>(collection, criteria, count);
    }

    /**
     * Получение уведомления пользователя
     *
     * @param messageId Идентификатор уведомления
     * @param username  Имя пользователя
     * @return Уведомление пользователя
     */
    public Feed getMessage(UUID messageId, String username) {
        return dsl
                .select(MESSAGE.fields())
                .select(MESSAGE_RECIPIENT.fields())
                .from(MESSAGE)
                .leftJoin(MESSAGE_RECIPIENT).on(MESSAGE_RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID).and(MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(username)))
                .where(MESSAGE.ID.cast(UUID.class).eq(messageId))
                .fetchOne(MAPPER);
    }

    /**
     * Прочтение и возврат уведомления пользователя
     *
     * @param messageId Идентификатор уведомления
     * @param username  Имя пользователя
     * @return Уведомление пользователя
     */
    @Transactional
    public Feed getMessageAndRead(UUID messageId, String username) {
        Feed result = getMessage(messageId, username);
        if (result != null)
            markRead(username, messageId);
        return result;
    }

    /**
     * Пометить все уведомления пользователя прочитанными
     *
     * @param tenantCode Код тенанта
     * @param username   Имя пользователя
     */
    @Transactional
    public void markReadAll(String tenantCode, String username) {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        dsl
                .update(MESSAGE_RECIPIENT)
                .set(MESSAGE_RECIPIENT.STATUS_TIME, now)
                .set(MESSAGE_RECIPIENT.STATUS, MessageStatusType.READ)
                .where(MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(username),
                        MESSAGE_RECIPIENT.STATUS.eq(MessageStatusType.SENT),
                        exists(dsl.selectOne().from(MESSAGE)
                                .where(MESSAGE.ID.eq(MESSAGE_RECIPIENT.MESSAGE_ID),
                                        MESSAGE.TENANT_CODE.eq(tenantCode))))
                .execute();
    }

    /**
     * Пометить уведомление пользователя прочитанными
     *
     * @param username  Имя пользователя
     * @param messageId Идентификатор уведомления
     */
    @Transactional
    public void markRead(String username, UUID messageId) {
        LocalDateTime now = LocalDateTime.now();
        dsl.update(MESSAGE_RECIPIENT)
                .set(MESSAGE_RECIPIENT.STATUS_TIME, now)
                .set(MESSAGE_RECIPIENT.STATUS, MessageStatusType.READ)
                .where(MESSAGE_RECIPIENT.MESSAGE_ID.eq(messageId),
                        MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(username),
                        MESSAGE_RECIPIENT.STATUS.eq(MessageStatusType.SENT))
                .execute();
    }

    /**
     * Получение количества непрочитанных уведомлений пользователем
     *
     * @param tenantCode Идентификатор системы
     * @param username   Имя пользователя
     * @return Количество непрочитанных уведомлений пользователем
     */
    public FeedCount getFeedCount(String tenantCode, String username) {
        Integer count = dsl
                .selectCount()
                .from(MESSAGE)
                .leftJoin(MESSAGE_RECIPIENT).on(MESSAGE_RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID))
                .leftJoin(CHANNEL).on(MESSAGE.CHANNEL_CODE.eq(CHANNEL.CODE))
                .where(
                        MESSAGE.TENANT_CODE.eq(tenantCode),
                        MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(username),
                        CHANNEL.IS_INTERNAL.eq(Boolean.TRUE),
                        MESSAGE_RECIPIENT.STATUS.eq(MessageStatusType.SENT))
                .fetchOne().value1();
        return new FeedCount(tenantCode, username, count);
    }
}
