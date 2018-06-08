package ru.inovus.messaging.impl;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import ru.inovus.messaging.api.Message;
import ru.inovus.messaging.api.UnreadMessagesInfo;
import ru.inovus.messaging.impl.jooq.Tables;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;
import ru.inovus.messaging.impl.rest.MessagingCriteria;
import ru.inovus.messaging.impl.rest.MessagingResponse;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static ru.inovus.messaging.impl.jooq.Sequences.MESSAGE_ID_SEQ;

@Service
public class MessageService {
    private static final RecordMapper<MessageRecord, Message> MAPPER = record -> {
        Message message = new Message();
        message.setId(String.valueOf(record.getId()));
        message.setCaption(record.getCaption());
        message.setText(record.getText());
        message.setAlertType(record.getAlertType());
        message.setSeverity(record.getSeverity());
        message.setReadAt(record.getReadAt());
        message.setSentAt(record.getSentAt());
        return message;
    };
    private final DSLContext dslContext;

    public MessageService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Message createMessage(Message message, String recipient) {
        InsertResultStep<MessageRecord> returning = dslContext
                .insertInto(Tables.MESSAGE)
                .values(MESSAGE_ID_SEQ.nextval(), message.getCaption(), message.getText(),
                        message.getSeverity(), message.getAlertType(), message.getSentAt(),
                        null, recipient)
                .returning();
        message.setId(String.valueOf(returning.fetch().get(0).getId()));
        return message;
    }

    public UnreadMessagesInfo getUnreadMessages(String recipient) {
        Integer count = dslContext
                .selectCount()
                .from(Tables.MESSAGE)
                .where(Tables.MESSAGE.READ_AT.isNull(), Tables.MESSAGE.RECIPIENT.eq(recipient))
                .fetchOne().value1();
        return new UnreadMessagesInfo(count);
    }

    public void markRead(String messageId) {
        dslContext.update(Tables.MESSAGE)
                .set(Tables.MESSAGE.READ_AT, LocalDateTime.now(Clock.systemUTC()))
                .where(Tables.MESSAGE.ID.eq(Integer.valueOf(messageId)))
                .execute();
    }

    public MessagingResponse getMessages(MessagingCriteria criteria) {
        Condition condition = Tables.MESSAGE.RECIPIENT.eq(criteria.getUser());
        SelectConditionStep<MessageRecord> query = dslContext.selectFrom(Tables.MESSAGE)
                .where(condition);
        int count = dslContext.fetchCount(query);
        List<Message> collection = query
                .orderBy(Tables.MESSAGE.SENT_AT.desc())
                .limit(criteria.getSize())
                .offset(criteria.getFirst())
                .fetch(MAPPER);
        return new MessagingResponse(count, collection);
    }

    public Message getMessage(String messageId) {
        return dslContext.selectFrom(Tables.MESSAGE)
                .where(Tables.MESSAGE.ID.cast(String.class).eq(messageId))
                .fetchOne(MAPPER);
    }

}
