package ru.inovus.messaging.impl.service;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inovus.messaging.api.criteria.RecipientGroupCriteria;
import ru.inovus.messaging.api.model.MessageTemplate;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.RecipientGroup;
import ru.inovus.messaging.impl.jooq.Sequences;
import ru.inovus.messaging.impl.jooq.tables.records.RecipientGroupRecord;
import ru.inovus.messaging.impl.jooq.tables.records.RecipientGroupTemplateRecord;
import ru.inovus.messaging.impl.jooq.tables.records.RecipientGroupUserRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.inovus.messaging.impl.jooq.Tables.RECIPIENT_GROUP;
import static ru.inovus.messaging.impl.jooq.Tables.RECIPIENT_GROUP_USER;
import static ru.inovus.messaging.impl.jooq.Tables.RECIPIENT_GROUP_TEMPLATE;

/**
 * Сервис групп получателей
 */
@Service
public class RecipientGroupService {

    @Autowired
    private DSLContext dsl;

    @SuppressWarnings("unchecked")
    RecordMapper<Record, RecipientGroup> MAPPER = rec -> {
        RecipientGroupRecord record = rec.into(RECIPIENT_GROUP);
        RecipientGroup result = new RecipientGroup();
        result.setId(record.getId());
        result.setName(record.getName());
        result.setDescription(record.getDescription());
        result.setTenantCode(record.getTenantCode());
        result.setRecipients((List<Recipient>) rec.get("users"));
        result.setTemplates((List<MessageTemplate>) rec.get("templates"));
        return result;
    };

    RecordMapper<Record, Recipient> RECIPIENT_MAPPER = rec -> {
        RecipientGroupUserRecord record = rec.into(RECIPIENT_GROUP_USER);
        Recipient result = new Recipient(record.getRecipientUsername());
        result.setName(record.getRecipientName());
        return result;
    };

    RecordMapper<Record, MessageTemplate> TEMPLATE_MAPPER = rec -> {
        RecipientGroupTemplateRecord record = rec.into(RECIPIENT_GROUP_TEMPLATE);
        MessageTemplate result = new MessageTemplate(record.getMessageTemplateId());
        result.setCode(record.getMessageTemplateCode());
        return result;
    };

    public Page<RecipientGroup> getRecipientGroups(String tenantCode, RecipientGroupCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(RECIPIENT_GROUP.TENANT_CODE.eq(tenantCode));
        Optional.ofNullable(criteria.getName()).ifPresent(name ->
                conditions.add(RECIPIENT_GROUP.NAME.eq(name)));

        var query = dsl
                .selectDistinct(RECIPIENT_GROUP.ID, RECIPIENT_GROUP.NAME, RECIPIENT_GROUP.DESCRIPTION, RECIPIENT_GROUP.TENANT_CODE,
                        DSL.multiset(
                                DSL.select(RECIPIENT_GROUP_USER.RECIPIENT_NAME, RECIPIENT_GROUP_USER.RECIPIENT_USERNAME)
                                        .from(RECIPIENT_GROUP_USER)
                                        .where(RECIPIENT_GROUP_USER.RECIPIENT_GROUP_ID.eq(RECIPIENT_GROUP.ID))
                        ).convertFrom(r -> r.map(RECIPIENT_MAPPER)).as("users"),
                        DSL.multiset(
                                DSL.select(RECIPIENT_GROUP_TEMPLATE.MESSAGE_TEMPLATE_ID, RECIPIENT_GROUP_TEMPLATE.MESSAGE_TEMPLATE_CODE)
                                        .from(RECIPIENT_GROUP_TEMPLATE)
                                        .where(RECIPIENT_GROUP_TEMPLATE.RECIPIENT_GROUP_ID.eq(RECIPIENT_GROUP.ID))
                        ).convertFrom(r -> r.map(TEMPLATE_MAPPER)).as("templates")
                )
                .from(RECIPIENT_GROUP);

        var countQuery = dsl.selectDistinct(RECIPIENT_GROUP.ID)
                .from(RECIPIENT_GROUP);

        if (criteria.getTemplateCodes() != null && !criteria.getTemplateCodes().isEmpty()) {
            List<Integer> targetTemplateCodes = criteria.getTemplateCodes();
            query.join(RECIPIENT_GROUP_TEMPLATE)
                    .on(RECIPIENT_GROUP_TEMPLATE.RECIPIENT_GROUP_ID.eq(RECIPIENT_GROUP.ID))
                    .where(RECIPIENT_GROUP_TEMPLATE.MESSAGE_TEMPLATE_ID.in(targetTemplateCodes));
            query.groupBy(RECIPIENT_GROUP.ID)
                    .having(DSL.countDistinct(RECIPIENT_GROUP_TEMPLATE.MESSAGE_TEMPLATE_ID).ge(targetTemplateCodes.size()));

            countQuery.join(RECIPIENT_GROUP_TEMPLATE)
                    .on(RECIPIENT_GROUP_TEMPLATE.RECIPIENT_GROUP_ID.eq(RECIPIENT_GROUP.ID))
                    .where(RECIPIENT_GROUP_TEMPLATE.MESSAGE_TEMPLATE_ID.in(targetTemplateCodes));
            countQuery.groupBy(RECIPIENT_GROUP.ID)
                    .having(DSL.countDistinct(RECIPIENT_GROUP_TEMPLATE.MESSAGE_TEMPLATE_ID).ge(targetTemplateCodes.size()));
        }

        if (criteria.getRecipientNames() != null && !criteria.getRecipientNames().isEmpty()) {
            List<String> targetRecipients = criteria.getRecipientNames();
            query.join(RECIPIENT_GROUP_USER)
                    .on(RECIPIENT_GROUP_USER.RECIPIENT_GROUP_ID.eq(RECIPIENT_GROUP.ID))
                    .where(RECIPIENT_GROUP_USER.RECIPIENT_USERNAME.in(targetRecipients));
            query.groupBy(RECIPIENT_GROUP.ID)
                    .having(DSL.countDistinct(RECIPIENT_GROUP_USER.RECIPIENT_USERNAME).ge(targetRecipients.size()));

            countQuery.join(RECIPIENT_GROUP_USER)
                    .on(RECIPIENT_GROUP_USER.RECIPIENT_GROUP_ID.eq(RECIPIENT_GROUP.ID))
                    .where(RECIPIENT_GROUP_USER.RECIPIENT_USERNAME.in(targetRecipients));
            countQuery.groupBy(RECIPIENT_GROUP.ID)
                    .having(DSL.countDistinct(RECIPIENT_GROUP_USER.RECIPIENT_USERNAME).ge(targetRecipients.size()));
        }

        List<RecipientGroup> list = query.where(conditions)
                .orderBy(RECIPIENT_GROUP.NAME)
                .limit(criteria.getPageSize())
                .offset((int) criteria.getOffset())
                .fetch(MAPPER);

        Integer count =dsl.selectCount().from(countQuery.where(conditions))
                .fetchOne().component1();
        return new PageImpl<>(list, criteria, count);
    }

    public RecipientGroup getRecipientGroup(String tenantCode, Integer id) {
        if (id == null) return null;
        return dsl
                .select(RECIPIENT_GROUP.ID, RECIPIENT_GROUP.NAME, RECIPIENT_GROUP.DESCRIPTION, RECIPIENT_GROUP.TENANT_CODE,
                        DSL.multiset(
                                DSL.select(RECIPIENT_GROUP_USER.RECIPIENT_NAME, RECIPIENT_GROUP_USER.RECIPIENT_USERNAME)
                                        .from(RECIPIENT_GROUP_USER)
                                        .where(RECIPIENT_GROUP_USER.RECIPIENT_GROUP_ID.eq(RECIPIENT_GROUP.ID))
                        ).convertFrom(r -> r.map(RECIPIENT_MAPPER)).as("users"),
                        DSL.multiset(
                                DSL.select(RECIPIENT_GROUP_TEMPLATE.MESSAGE_TEMPLATE_ID, RECIPIENT_GROUP_TEMPLATE.MESSAGE_TEMPLATE_CODE)
                                        .from(RECIPIENT_GROUP_TEMPLATE)
                                        .where(RECIPIENT_GROUP_TEMPLATE.RECIPIENT_GROUP_ID.eq(RECIPIENT_GROUP.ID))
                        ).convertFrom(r -> r.map(TEMPLATE_MAPPER)).as("templates")
                )
                .from(RECIPIENT_GROUP)
                .where(RECIPIENT_GROUP.ID.eq(id), RECIPIENT_GROUP.TENANT_CODE.eq(tenantCode))
                .fetchOne(MAPPER);
    }

    @Transactional
    public Integer createRecipientGroup(String tenantCode, RecipientGroup value) {
        Integer id = dsl.nextval(Sequences.RECIPIENT_GROUP_ID_SEQ).intValue();
        dsl.insertInto(RECIPIENT_GROUP)
                .columns(RECIPIENT_GROUP.ID, RECIPIENT_GROUP.NAME, RECIPIENT_GROUP.DESCRIPTION, RECIPIENT_GROUP.TENANT_CODE)
                .values(id, value.getName(), value.getDescription(), tenantCode)
                .execute();

        insertRecipients(id, value.getRecipients());
        insertTemplates(id, value.getTemplates());
        return id;
    }

    @Transactional
    public void updateRecipientGroup(String tenantCode, Integer id, RecipientGroup value) {
        dsl.update(RECIPIENT_GROUP)
                .set(RECIPIENT_GROUP.NAME, value.getName())
                .set(RECIPIENT_GROUP.DESCRIPTION, value.getDescription())
                .set(RECIPIENT_GROUP.TENANT_CODE, tenantCode)
                .where(RECIPIENT_GROUP.ID.eq(id), RECIPIENT_GROUP.TENANT_CODE.eq(tenantCode))
                .execute();

        deleteDependency(id);
        insertRecipients(id, value.getRecipients());
        insertTemplates(id, value.getTemplates());
    }

    @Transactional
    public void deleteRecipientGroup(String tenantCode, Integer id) {
        deleteDependency(id);
        dsl.deleteFrom(RECIPIENT_GROUP)
                .where(RECIPIENT_GROUP.ID.eq(id), RECIPIENT_GROUP.TENANT_CODE.eq(tenantCode))
                .execute();
    }

    private void deleteDependency(Integer grpId) {
        dsl.deleteFrom(RECIPIENT_GROUP_USER)
                .where(RECIPIENT_GROUP_USER.RECIPIENT_GROUP_ID.eq(grpId))
                .execute();
        dsl.deleteFrom(RECIPIENT_GROUP_TEMPLATE)
                .where(RECIPIENT_GROUP_TEMPLATE.RECIPIENT_GROUP_ID.eq(grpId))
                .execute();
    }

    private void insertRecipients(Integer grpId, List<Recipient> recipients) {
        if (recipients != null && !recipients.isEmpty()) {
            List<RecipientGroupUserRecord> records = recipients.stream()
                    .map(i -> fromRecipient(grpId, i)).collect(Collectors.toList());
            dsl.batchInsert(records).execute();
        }
    }

    private void insertTemplates(Integer grpId, List<MessageTemplate> templates) {
        if (templates != null && !templates.isEmpty()) {
            List<RecipientGroupTemplateRecord> records = templates.stream()
                    .map(i -> fromTemplate(grpId, i)).collect(Collectors.toList());
            dsl.batchInsert(records).execute();
        }
    }

    private RecipientGroupUserRecord fromRecipient(Integer grpId, Recipient recipient) {
        RecipientGroupUserRecord result = dsl.newRecord(RECIPIENT_GROUP_USER);
        result.setRecipientGroupId(grpId);
        result.setRecipientName(recipient.getName());
        result.setRecipientUsername(recipient.getUsername());
        return result;
    }

    private RecipientGroupTemplateRecord fromTemplate(Integer id, MessageTemplate mt) {
        RecipientGroupTemplateRecord result = dsl.newRecord(RECIPIENT_GROUP_TEMPLATE);
        result.setRecipientGroupId(id);
        result.setMessageTemplateId(mt.getId());
        result.setMessageTemplateCode(mt.getCode());
        return result;
    }

}
