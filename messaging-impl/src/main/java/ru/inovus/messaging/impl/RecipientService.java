package ru.inovus.messaging.impl;

import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.impl.jooq.tables.records.RecipientRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.inovus.messaging.impl.jooq.Tables.MESSAGE_SETTING;
import static ru.inovus.messaging.impl.jooq.Tables.RECIPIENT;

@Service
public class RecipientService {

    private static final RecordMapper<Record, Recipient> MAPPER = rec -> {
        RecipientRecord record = rec.into(RECIPIENT);
        Recipient recipient = new Recipient();
        recipient.setId(record.getId());
        recipient.setEmail(record.getEmail());
        recipient.setMessageId(record.getMessageId());
        recipient.setReadAt(record.getReadAt());
        recipient.setRecipient(record.getRecipient());
        return recipient;
    };
    private final DSLContext dsl;

    public RecipientService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Page<Recipient> getRecipients(RecipientCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        Optional.ofNullable(criteria.getMessageId())
                .ifPresent(messageId -> conditions.add(RECIPIENT.MESSAGE_ID.eq(messageId)));
        SelectConditionStep<Record> query = dsl
                .select(RECIPIENT.fields())
                .from(RECIPIENT)
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

}
