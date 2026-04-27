package ru.inovus.messaging.impl.service;

import org.jooq.Record;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jooq.impl.DSL;
import ru.inovus.messaging.api.criteria.FeedCriteria;
import ru.inovus.messaging.api.model.Feed;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.api.model.FeedStatistics;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.api.model.enums.MessageType;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecipientRecord;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        message.setTemplateCode(record.getTemplateCode());
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
        conditions.add(MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(username));
        Optional.ofNullable(criteria.getSeverity())
                .ifPresent(severity -> conditions.add(MESSAGE.SEVERITY.eq(severity)));
        Optional.ofNullable(criteria.getSentAtBegin())
                .ifPresent(start -> conditions.add(MESSAGE.SENT_AT.greaterOrEqual(start)));
        Optional.ofNullable(criteria.getSentAtEnd())
                .ifPresent(end -> conditions.add(MESSAGE.SENT_AT.lessOrEqual(end)));
        Optional.ofNullable(criteria.getMessageType())
                .ifPresent(messageType -> conditions.add(MESSAGE.MESSAGE_TYPE.eq(messageType)));
        Optional.ofNullable(criteria.getRecipientType())
                .ifPresent(recipientType -> conditions.add(MESSAGE.RECIPIENT_TYPE.eq(recipientType)));

        if (Boolean.TRUE.equals(criteria.getIsRead())) {
            conditions.add(MESSAGE_RECIPIENT.STATUS.eq(MessageStatusType.READ));
        } else if (Boolean.FALSE.equals(criteria.getIsRead())) {
            conditions.add(MESSAGE_RECIPIENT.STATUS.ne(MessageStatusType.READ));
        }

        SelectConditionStep<Record> query = dsl
                .select(MESSAGE.fields())
                .select(MESSAGE_RECIPIENT.fields())
                .from(MESSAGE)
                .join(MESSAGE_RECIPIENT).on(MESSAGE_RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID))
                .join(CHANNEL).on(CHANNEL.CODE.eq(MESSAGE.CHANNEL_CODE))
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
     * @param criteria   Критерии фильтрации уведомлений
     */
    @Transactional
    public void markReadAll(String tenantCode, String username, FeedCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(MESSAGE_RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID));
        conditions.add(MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(username));
        conditions.add(MESSAGE_RECIPIENT.STATUS.ne(MessageStatusType.READ));
        conditions.add(MESSAGE.TENANT_CODE.eq(tenantCode));
        Optional.ofNullable(criteria.getMessageType())
                .ifPresent(messageType -> conditions.add(MESSAGE.MESSAGE_TYPE.eq(messageType)));
        Optional.ofNullable(criteria.getRecipientType())
                .ifPresent(recipientType -> conditions.add(MESSAGE.RECIPIENT_TYPE.eq(recipientType)));

        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        dsl
                .update(MESSAGE_RECIPIENT)
                .set(MESSAGE_RECIPIENT.STATUS_TIME, now)
                .set(MESSAGE_RECIPIENT.STATUS, MessageStatusType.READ)
                .from(MESSAGE)
                .where(conditions)
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
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        dsl.update(MESSAGE_RECIPIENT)
                .set(MESSAGE_RECIPIENT.STATUS_TIME, now)
                .set(MESSAGE_RECIPIENT.STATUS, MessageStatusType.READ)
                .where(MESSAGE_RECIPIENT.MESSAGE_ID.eq(messageId),
                        MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(username))
                .execute();
    }

    /**
     * Получение счётчиков уведомлений пользователя
     *
     * @param tenantCode Код тенанта
     * @param username   Имя пользователя
     * @return Счётчики уведомлений
     */
    public FeedStatistics getFeedStatistics(String tenantCode, String username) {
        return dsl
                .select(
                        DSL.count(),
                        DSL.count().filterWhere(MESSAGE_RECIPIENT.STATUS.eq(MessageStatusType.READ)),
                        DSL.count().filterWhere(MESSAGE_RECIPIENT.STATUS.eq(MessageStatusType.SENT)),
                        DSL.count().filterWhere(MESSAGE.MESSAGE_TYPE.eq(MessageType.USER)),
                        DSL.count().filterWhere(MESSAGE.MESSAGE_TYPE.eq(MessageType.SYSTEM)),
                        DSL.count().filterWhere(MESSAGE.RECIPIENT_TYPE.eq(RecipientType.RECIPIENT)),
                        DSL.count().filterWhere(MESSAGE.RECIPIENT_TYPE.eq(RecipientType.USER_GROUP_BY_ROLE))
                )
                .from(MESSAGE)
                .join(MESSAGE_RECIPIENT).on(MESSAGE_RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID))
                .join(CHANNEL).on(CHANNEL.CODE.eq(MESSAGE.CHANNEL_CODE))
                .where(
                        MESSAGE.TENANT_CODE.eq(tenantCode),
                        MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(username),
                        CHANNEL.IS_INTERNAL.eq(Boolean.TRUE)
                )
                .fetchOne(record -> {
                    FeedStatistics stats = new FeedStatistics();
                    stats.setTotal(record.value1());
                    stats.setRead(record.value2());
                    stats.setUnread(record.value3());
                    stats.setUser(record.value4());
                    stats.setSystem(record.value5());
                    stats.setRecipient(record.value6());
                    stats.setUserGroupByRole(record.value7());
                    return stats;
                });
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
                .join(MESSAGE_RECIPIENT).on(MESSAGE_RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID))
                .join(CHANNEL).on(MESSAGE.CHANNEL_CODE.eq(CHANNEL.CODE))
                .where(
                        MESSAGE.TENANT_CODE.eq(tenantCode),
                        MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(username),
                        CHANNEL.IS_INTERNAL.eq(Boolean.TRUE),
                        MESSAGE_RECIPIENT.STATUS.eq(MessageStatusType.SENT))
                .fetchOne().value1();
        return new FeedCount(tenantCode, username, count);
    }
}
