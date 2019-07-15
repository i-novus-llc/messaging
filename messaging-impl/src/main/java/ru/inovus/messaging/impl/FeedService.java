package ru.inovus.messaging.impl;

import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inovus.messaging.api.UnreadMessagesInfo;
import ru.inovus.messaging.api.criteria.FeedCriteria;
import ru.inovus.messaging.api.model.Component;
import ru.inovus.messaging.api.model.Feed;
import ru.inovus.messaging.api.model.InfoType;
import ru.inovus.messaging.api.model.RecipientType;
import ru.inovus.messaging.impl.jooq.tables.records.ComponentRecord;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;
import ru.inovus.messaging.impl.jooq.tables.records.RecipientRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        conditions.add(MESSAGE.RECIPIENT_TYPE.eq(RecipientType.ALL).or(RECIPIENT.ID.isNotNull()));
        Optional.ofNullable(criteria.getSystemId())
                .ifPresent(systemId -> conditions.add(MESSAGE.SYSTEM_ID.eq(systemId)));
        Optional.ofNullable(criteria.getComponentId())
                .ifPresent(componentId -> conditions.add(MESSAGE.COMPONENT_ID.eq(componentId)));
        Optional.ofNullable(criteria.getSeverity())
                .ifPresent(severity -> conditions.add(MESSAGE.SEVERITY.eq(severity)));
        Optional.ofNullable(criteria.getSentAtBegin())
                .ifPresent(start -> conditions.add(MESSAGE.SENT_AT.greaterOrEqual(start)));
        Optional.ofNullable(criteria.getSentAtEnd())
                .ifPresent(end -> conditions.add(MESSAGE.SENT_AT.lessOrEqual(end)));

        SelectConditionStep<Record> query = dsl
                .select(MESSAGE.fields())
                .select(COMPONENT.fields())
                .select(RECIPIENT.fields())
                .from(MESSAGE)
                .leftJoin(COMPONENT).on(COMPONENT.ID.eq(MESSAGE.COMPONENT_ID))
                .leftJoin(RECIPIENT).on(RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID).and(RECIPIENT.RECIPIENT_.eq(recipient)))
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

    public Feed getMessage(String messageId, String recipient) {
        return dsl
                .select(MESSAGE.fields())
                .select(COMPONENT.fields())
                .select(RECIPIENT.fields())
                .from(MESSAGE)
                .leftJoin(COMPONENT).on(COMPONENT.ID.eq(MESSAGE.COMPONENT_ID))
                .leftJoin(RECIPIENT).on(RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID).and(RECIPIENT.RECIPIENT_.eq(recipient)))
                .where(MESSAGE.ID.cast(String.class).eq(messageId))
                .fetchOne(MAPPER);
    }

    @Transactional
    public Feed getMessageAndRead(String messageId, String recipient) {
        markRead(recipient, messageId);
        return getMessage(messageId, recipient);
    }

    @Transactional
    public void markReadAll(String recipient, String systemId) {
        LocalDateTime now = LocalDateTime.now();
        // Update 'personal' messages
        dsl
                .update(RECIPIENT)
                .set(RECIPIENT.READ_AT, now)
                .where(RECIPIENT.RECIPIENT_.eq(recipient).and(RECIPIENT.READ_AT.isNull()),
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
                    .set(RECIPIENT.ID, RECIPIENT_ID_SEQ.nextval())
                    .set(RECIPIENT.READ_AT, now)
                    .set(RECIPIENT.MESSAGE_ID, id)
                    .set(RECIPIENT.RECIPIENT_, recipient)
                    .execute();
        }
    }

    @Transactional
    public void markRead(String recipient, String... messageId) {
        LocalDateTime now = LocalDateTime.now();
        int updated = dsl
                .update(RECIPIENT)
                .set(RECIPIENT.READ_AT, now)
                .where(RECIPIENT.MESSAGE_ID.in(messageId))
                .execute();
        if (updated == 0 && messageId != null) {
            for (String id : messageId) {
                dsl
                        .insertInto(RECIPIENT)
                        .set(RECIPIENT.ID, RECIPIENT_ID_SEQ.nextval())
                        .set(RECIPIENT.READ_AT, now)
                        .set(RECIPIENT.MESSAGE_ID, id)
                        .set(RECIPIENT.RECIPIENT_, recipient)
                        .execute();
            }
        }
    }

    public UnreadMessagesInfo getFeedCount(String recipient, String systemId) {
        Integer count = dsl
                .selectCount()
                .from(MESSAGE)
                .leftJoin(RECIPIENT).on(RECIPIENT.MESSAGE_ID.eq(MESSAGE.ID).and(RECIPIENT.RECIPIENT_.like(recipient)))
                .where(MESSAGE.RECIPIENT_TYPE.eq(RecipientType.ALL).and(RECIPIENT.ID.isNull())
                                .or(MESSAGE.RECIPIENT_TYPE.eq(RecipientType.USER).and(RECIPIENT.READ_AT.isNull())
                                        .and(RECIPIENT.ID.isNull())),
                        MESSAGE.SYSTEM_ID.eq(systemId))
                .fetchOne().value1();
        return new UnreadMessagesInfo(count);
    }

    private static Feed mapFeed(Record rec) {
        MessageRecord record = rec.into(MESSAGE);
        ComponentRecord componentRecord = rec.into(COMPONENT);
        RecipientRecord recipientRecord = rec.into(RECIPIENT);
        Feed message = new Feed();
        message.setId(String.valueOf(record.getId()));
        message.setCaption(record.getCaption());
        message.setText(record.getText());
        message.setSeverity(record.getSeverity());
        message.setSentAt(record.getSentAt());
        List<InfoType> infoTypes = new ArrayList<>();
        if (record.getSendNotice() != null && record.getSendNotice()) {
            infoTypes.add(InfoType.NOTICE);
        }
        if (record.getSendEmail() != null && record.getSendEmail()) {
            infoTypes.add(InfoType.EMAIL);
        }
        message.setSystemId(record.getSystemId());
        if (componentRecord != null) {
            message.setComponent(new Component(componentRecord.getId(), componentRecord.getName()));
        }
        message.setReadAt(recipientRecord.getReadAt());
        return message;
    }

}
