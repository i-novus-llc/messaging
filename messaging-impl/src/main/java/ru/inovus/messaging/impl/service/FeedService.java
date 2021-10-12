package ru.inovus.messaging.impl.service;

import org.jooq.Record;
import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inovus.messaging.api.criteria.FeedCriteria;
import ru.inovus.messaging.api.model.Feed;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecipientRecord;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.notExists;
import static ru.inovus.messaging.impl.jooq.Sequences.RECIPIENT_ID_SEQ;
import static ru.inovus.messaging.impl.jooq.Tables.*;

@Service
public class FeedService {
    private static final RecordMapper<Record, Feed> MAPPER = FeedService::mapFeed;
    private final DSLContext dsl;

    public FeedService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Page<Feed> getMessageFeed(String recipient, FeedCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(MESSAGE.RECIPIENT_TYPE.eq(RecipientType.ALL).or(MESSAGE_RECIPIENT.ID.isNotNull()));
        conditions.add(CHANNEL.IS_INTERNAL.eq(Boolean.TRUE));
        Optional.ofNullable(criteria.getTenantCode())
                .ifPresent(tenantCode -> conditions.add(MESSAGE.TENANT_CODE.eq(tenantCode)));
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
                .leftJoin(MESSAGE_RECIPIENT).on(MESSAGE_RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID).and(MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(recipient)))
                .leftJoin(CHANNEL).on(CHANNEL.ID.eq(MESSAGE.CHANNEL_ID))
                .where(conditions);
        int count = dsl.fetchCount(query);
        Field fieldSentAt = MESSAGE.field("sent_at");
        List<Feed> collection = query
                .orderBy(fieldSentAt.desc())
                .limit(criteria.getPageSize())
                .offset((int) criteria.getOffset())
                .fetch(MAPPER);
        return new PageImpl<>(collection, criteria, count);
    }

    public Feed getMessage(UUID messageId, String recipient) {
        return dsl
                .select(MESSAGE.fields())
                .select(MESSAGE_RECIPIENT.fields())
                .from(MESSAGE)
                .leftJoin(MESSAGE_RECIPIENT).on(MESSAGE_RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID).and(MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(recipient)))
                .where(MESSAGE.ID.cast(UUID.class).eq(messageId), MESSAGE.RECIPIENT_TYPE.eq(RecipientType.ALL)
                        .or(MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(recipient)))
                .fetchOne(MAPPER);
    }

    @Transactional
    public Feed getMessageAndRead(UUID messageId, String recipient) {
        Feed result = getMessage(messageId, recipient);
        if (result != null) {
            markRead(recipient, messageId);
        }
        return result;
    }

    @Transactional
    public void markReadAll(String recipient, String tenantCode) {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        // Update 'personal' messages
        dsl
                .update(MESSAGE_RECIPIENT)
                .set(MESSAGE_RECIPIENT.STATUS_TIME, now)
                .set(MESSAGE_RECIPIENT.STATUS, MessageStatusType.READ)
                .where(MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(recipient).and(MESSAGE_RECIPIENT.STATUS_TIME.isNull()),
                        exists(dsl.selectOne().from(MESSAGE)
                                .where(MESSAGE.ID.eq(MESSAGE_RECIPIENT.MESSAGE_ID),
                                        MESSAGE.TENANT_CODE.eq(tenantCode))))
                .execute();
        // Update messages 'for all'
        List<UUID> ids = dsl
                .select(MESSAGE.ID)
                .from(MESSAGE)
                .where(MESSAGE.RECIPIENT_TYPE.eq(RecipientType.ALL),
                        MESSAGE.TENANT_CODE.eq(tenantCode),
                        notExists(dsl.selectOne().from(MESSAGE_RECIPIENT)
                                .where(MESSAGE_RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID),
                                        MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(recipient))))
                .fetch().map(Record1::component1);
        for (UUID id : ids) {
            dsl
                    .insertInto(MESSAGE_RECIPIENT)
                    .set(MESSAGE_RECIPIENT.ID, dsl.nextval(RECIPIENT_ID_SEQ).intValue())
                    .set(MESSAGE_RECIPIENT.STATUS_TIME, now)
                    .set(MESSAGE_RECIPIENT.STATUS, MessageStatusType.READ)
                    .set(MESSAGE_RECIPIENT.MESSAGE_ID, id)
                    .set(MESSAGE_RECIPIENT.RECIPIENT_USERNAME, recipient)
                    .execute();
        }
    }

    @Transactional
    public void markRead(String recipient, UUID messageId) {
        if (messageId != null) {
            LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
            int updated = dsl.update(MESSAGE_RECIPIENT)
                    .set(MESSAGE_RECIPIENT.STATUS_TIME, now)
                    .set(MESSAGE_RECIPIENT.STATUS, MessageStatusType.READ)
                    .where(MESSAGE_RECIPIENT.MESSAGE_ID.eq(messageId)).and(MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(recipient))
                    .execute();
            if (updated == 0) {
                dsl.insertInto(MESSAGE_RECIPIENT)
                        .set(MESSAGE_RECIPIENT.ID, dsl.nextval(RECIPIENT_ID_SEQ).intValue())
                        .set(MESSAGE_RECIPIENT.STATUS_TIME, now)
                        .set(MESSAGE_RECIPIENT.MESSAGE_ID, messageId)
                        .set(MESSAGE_RECIPIENT.RECIPIENT_USERNAME, recipient)
                        .execute();
            }
        }
    }

    /**
     * Получение количества непрочитанных уведомлений пользователем
     *
     * @param username   Имя пользователя
     * @param tenantCode Идентификатор системы
     * @return Количество непрочитанных уведомлений пользователем
     */
    public FeedCount getFeedCount(String username, String tenantCode) {
        Integer count = dsl
                .selectCount()
                .from(MESSAGE)
                .leftJoin(MESSAGE_RECIPIENT).on(MESSAGE_RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID))
                .leftJoin(CHANNEL).on(MESSAGE.CHANNEL_ID.eq(CHANNEL.ID))
                .where(
                        MESSAGE.TENANT_CODE.eq(tenantCode),
                        MESSAGE_RECIPIENT.RECIPIENT_USERNAME.eq(username),
                        CHANNEL.IS_INTERNAL.eq(Boolean.TRUE),
                        MESSAGE_RECIPIENT.STATUS.eq(MessageStatusType.SENT))
                .fetchOne().value1();
        return new FeedCount(tenantCode, username, count);
    }

    private static Feed mapFeed(Record rec) {
        if (rec == null) return null;
        MessageRecord record = rec.into(MESSAGE);
        MessageRecipientRecord recipientRecord = rec.into(MESSAGE_RECIPIENT);
        Feed message = new Feed();
        message.setId(String.valueOf(record.getId()));
        message.setCaption(record.getCaption());
        message.setText(record.getText());
        message.setSeverity(record.getSeverity());
        message.setSentAt(record.getSentAt());
        message.setTenantCode(record.getTenantCode());
        message.setReadAt(recipientRecord.getStatusTime());
        return message;
    }
}
