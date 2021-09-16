package ru.inovus.messaging.impl.service;

import org.jooq.Record;
import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.MessageStatus;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecipientRecord;

import java.util.*;
import java.util.stream.Collectors;

import static ru.inovus.messaging.impl.jooq.Tables.MESSAGE_RECIPIENT;
import static ru.inovus.messaging.impl.jooq.Tables.MESSAGE_SETTING;

@Service
public class RecipientService {

    private static final RecordMapper<Record, Recipient> MAPPER = rec -> {
        MessageRecipientRecord record = rec.into(MESSAGE_RECIPIENT);
        Recipient recipient = new Recipient();
        recipient.setEmail(record.getRecipientSendChannelId());
        recipient.setMessageId(record.getMessageId());
        recipient.setReadAt(record.getReadAt());
        recipient.setName(record.getRecipientName());
        recipient.setStatus(record.getStatus());
        recipient.setDeparturedAt(record.getDeparturedAt());
        recipient.setSendMessageError(record.getSendMessageError());
        return recipient;
    };
    private final DSLContext dsl;

    public RecipientService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Page<Recipient> getRecipients(RecipientCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        Optional.ofNullable(criteria.getMessageId())
                .ifPresent(messageId -> conditions.add(MESSAGE_RECIPIENT.MESSAGE_ID.eq(messageId)));
        SelectConditionStep<Record> query = dsl
                .select(MESSAGE_RECIPIENT.fields())
                .from(MESSAGE_RECIPIENT)
                .where(conditions);
        int count = dsl.fetchCount(query);
        List<Recipient> collection = query
                .orderBy(getSortFields(criteria.getSort()))
                .limit(criteria.getPageSize())
                .offset((int) criteria.getOffset())
                .fetch(MAPPER);
        return new PageImpl<>(collection, criteria, count);
    }

    private Collection<SortField<?>> getSortFields(Sort sort) {
        Collection<SortField<?>> querySortFields = new ArrayList<>();
        if (sort.isEmpty()) {
            return querySortFields;
        }

        sort.get().map(s -> {
            Field field = MESSAGE_SETTING.field(s.getProperty());
            return s.getDirection().equals(Sort.Direction.ASC) ?
                    field.asc() : field.desc();
        }).collect(Collectors.toList());

        return querySortFields;
    }

    /**
     * Обновление статуса получателя уведомления
     *
     * @param messageId        Идентификатор сообщения
     * @param status           Статус уведомления
     * @param sendErrorMessage Сообщение ошибки отправки уведомления
     */
    public void updateStatus(UUID messageId, MessageStatus status, String sendErrorMessage) {
        dsl
                .update(MESSAGE_RECIPIENT)
                .set(MESSAGE_RECIPIENT.STATUS, status)
                .set(MESSAGE_RECIPIENT.SEND_MESSAGE_ERROR, sendErrorMessage)
                .where(MESSAGE_RECIPIENT.MESSAGE_ID.eq(messageId))
                .execute();
    }
}
