package ru.inovus.messaging.server.rest;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.BaseMessagingCriteria;
import ru.inovus.messaging.api.criteria.ComponentCriteria;
import ru.inovus.messaging.api.model.Component;
import ru.inovus.messaging.api.rest.ComponentRest;
import ru.inovus.messaging.impl.jooq.tables.records.ComponentRecord;

import java.util.List;

import static ru.inovus.messaging.impl.jooq.Tables.COMPONENT;

@Controller
public class ComponentRestImpl implements ComponentRest {

    private static final RecordMapper<Record, Component> MAPPER = rec -> {
        ComponentRecord record = rec.into(COMPONENT);
        return new Component(record.getId(), record.getName());
    };

    @Autowired
    private DSLContext dsl;

    @Override
    public Page<Component> getComponents(ComponentCriteria criteria) {
        List<Component> list = (criteria == null || criteria.getName() == null) ?
                dsl.selectFrom(COMPONENT).fetch(MAPPER) :
                dsl.selectFrom(COMPONENT).where(COMPONENT.NAME.containsIgnoreCase(criteria.getName())).fetch(MAPPER);
        return new PageImpl<>(list, new BaseMessagingCriteria(), list.size());
    }

}
