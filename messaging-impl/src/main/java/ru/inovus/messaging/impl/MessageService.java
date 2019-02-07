package ru.inovus.messaging.impl;

import net.n2oapp.criteria.api.Direction;
import net.n2oapp.criteria.api.Sorting;
import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.inovus.messaging.api.Message;
import ru.inovus.messaging.api.UnreadMessagesInfo;
import ru.inovus.messaging.impl.jooq.Tables;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;
import ru.inovus.messaging.api.MessagingCriteria;
import ru.inovus.messaging.impl.rest.MessagingResponse;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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
    private final DSLContext dsl;

    public MessageService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Message createMessage(Message message, String recipient, String systemId) {
        InsertResultStep<MessageRecord> returning = dsl
                .insertInto(Tables.MESSAGE)
                .values(message.getId() != null ? message.getId() : MESSAGE_ID_SEQ.nextval(),
                        message.getCaption(), message.getText(),
                        message.getSeverity(), message.getAlertType(), message.getSentAt(),
                        null, recipient, systemId)
                .returning();
        message.setId(String.valueOf(returning.fetch().get(0).getId()));
        return message;
    }

    public UnreadMessagesInfo getUnreadMessages(String recipient, String systemId) {
        Integer count = dsl
                .selectCount()
                .from(Tables.MESSAGE)
                .where(
                        Tables.MESSAGE.READ_AT.isNull(),
                        Tables.MESSAGE.RECIPIENT.eq(recipient),
                        Tables.MESSAGE.SYSTEM_ID.eq(systemId))
                .fetchOne().value1();
        return new UnreadMessagesInfo(count);
    }

    public void markRead(String systemId, String... messageId) {
        dsl
                .update(Tables.MESSAGE)
                .set(Tables.MESSAGE.READ_AT, LocalDateTime.now(Clock.systemUTC()))
                .where(Tables.MESSAGE.ID.in(messageId),
                        Tables.MESSAGE.SYSTEM_ID.eq(systemId))
                .execute();
    }

    public void markReadAll(String recipient, String systemId) {
        dsl
                .update(Tables.MESSAGE)
                .set(Tables.MESSAGE.READ_AT, LocalDateTime.now(Clock.systemUTC()))
                .where(Tables.MESSAGE.RECIPIENT.eq(recipient), Tables.MESSAGE.SYSTEM_ID.eq(systemId))
                .execute();
    }

    public Page<Message> getMessages(MessagingCriteria criteria) {
        Condition condition = Tables.MESSAGE.RECIPIENT.eq(criteria.getUser())
                .and(Tables.MESSAGE.SYSTEM_ID.eq(criteria.getSystemId()));
        SelectConditionStep<MessageRecord> query = dsl
                .selectFrom(Tables.MESSAGE)
                .where(condition);
        int count = dsl.fetchCount(query);
        List<Message> collection = query
                .orderBy(getSortFields(criteria.getOrders()))
                .limit(criteria.getPageSize())
                .offset((int)criteria.getOffset())
                .fetch(MAPPER);
        return new PageImpl<>(collection, criteria, count);
    }

    public Message getMessage(String messageId) {
        return dsl
                .selectFrom(Tables.MESSAGE)
                .where(Tables.MESSAGE.ID.cast(String.class).eq(messageId))
                .fetchOne(MAPPER);
    }

    private Collection<SortField<?>> getSortFields(List<Sort.Order> sortingList) {
        Collection<SortField<?>> querySortFields = new ArrayList<>();

        if (sortingList == null) {
            return querySortFields;
        }

        for (Sort.Order sorting : sortingList) {
            Field field = Tables.MESSAGE.field(sorting.getProperty());
            SortField<?> querySortField = convertFieldToSortField(field, sorting.getDirection());
            querySortFields.add(querySortField);
        }

        return querySortFields;
    }

    private SortField<?> convertFieldToSortField(Field field, Sort.Direction sortDirection) {
        if (sortDirection == Sort.Direction.ASC) {
            return field.asc();
        } else {
            return field.desc();
        }
    }

}
