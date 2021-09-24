package ru.inovus.messaging.impl.service;

import org.jooq.Record;
import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecipientRecord;

import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.exists;
import static ru.inovus.messaging.impl.jooq.Tables.*;

/**
 * Сервис получателей уведомлений
 */
@Service
public class RecipientService {

    private final DSLContext dsl;

    private static final RecordMapper<Record, Recipient> MAPPER = rec -> {
        MessageRecipientRecord record = rec.into(MESSAGE_RECIPIENT);
        Recipient recipient = new Recipient();
        recipient.setUsername(record.getRecipientSendChannelId());
        recipient.setMessageId(record.getMessageId());
        recipient.setReadAt(record.getReadAt());
        recipient.setName(record.getRecipientName());
        recipient.setStatus(record.getStatus());
        recipient.setDeparturedAt(record.getDeparturedAt());
        recipient.setErrorMessage(record.getSendMessageError());
        return recipient;
    };

    public RecipientService(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Получение списка получателей уведомлений по критерию
     *
     * @param criteria Критерий получателей
     * @return Список получателей уведомлений
     */
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

    /**
     * Получение списка полей, по которым будет производиться сортировка
     *
     * @param sort Вариант сортировки
     * @return Список полей, по которым будет производиться сортировка
     */
    private Collection<SortField<?>> getSortFields(Sort sort) {
        if (sort.isEmpty())
            return new ArrayList<>();

        return sort.get().map(s -> {
            Field field = MESSAGE_RECIPIENT.field(s.getProperty());
            return (SortField<?>) (s.getDirection().equals(Sort.Direction.ASC) ?
                    field.asc() : field.desc());
        }).collect(Collectors.toList());
    }

    /**
     * Обновление статуса получателя уведомления
     *
     * @param status Статус уведомления
     */
    public void updateStatus(MessageStatus status) {
        List<Condition> conditions = new ArrayList<>();
        Optional.ofNullable(status.getUsername())
                .ifPresent(username -> conditions.add(MESSAGE_RECIPIENT.RECIPIENT_SEND_CHANNEL_ID.eq(username)));
        // change status if previous status is correct (e.g. can't change FAILED to READ)
        conditions.add(MESSAGE_RECIPIENT.STATUS.eq(status.getStatus().getPrevStatus()));
        if (status.getMessageId() != null) {
            conditions.add(MESSAGE_RECIPIENT.MESSAGE_ID.eq(UUID.fromString(status.getMessageId())));
            conditions.add(exists(dsl.selectOne().from(MESSAGE)
                    .where(MESSAGE.ID.eq(MESSAGE_RECIPIENT.MESSAGE_ID),
                            MESSAGE.SYSTEM_ID.eq(status.getSystemId()))));
        } else
            conditions.add(exists(dsl.selectOne().from(MESSAGE)
                    .where(MESSAGE.ID.eq(MESSAGE_RECIPIENT.MESSAGE_ID),
                            MESSAGE.SYSTEM_ID.eq(status.getSystemId())
                                    .andExists(dsl.selectOne().from(CHANNEL)
                                            .where(CHANNEL.ID.eq(MESSAGE.CHANNEL_ID)
                                            )))));

        dsl
                .update(MESSAGE_RECIPIENT)
                .set(MESSAGE_RECIPIENT.STATUS, status.getStatus())
                .set(MESSAGE_RECIPIENT.SEND_MESSAGE_ERROR, status.getErrorMessage())
                .where(conditions)
                .execute();
    }
}
