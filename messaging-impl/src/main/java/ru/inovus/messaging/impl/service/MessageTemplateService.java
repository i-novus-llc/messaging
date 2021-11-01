package ru.inovus.messaging.impl.service;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inovus.messaging.api.criteria.MessageTemplateCriteria;
import ru.inovus.messaging.api.model.Channel;
import ru.inovus.messaging.api.model.MessageTemplate;
import ru.inovus.messaging.impl.jooq.tables.records.MessageTemplateRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.inovus.messaging.impl.jooq.Sequences.MESSAGE_TEMPLATE_ID_SEQ;
import static ru.inovus.messaging.impl.jooq.Tables.MESSAGE_TEMPLATE;

/**
 * Сервис шаблонов уведомлений
 */
@Service
public class MessageTemplateService {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private ChannelService channelService;

    RecordMapper<Record, MessageTemplate> MAPPER = rec -> {
        MessageTemplateRecord record = rec.into(MESSAGE_TEMPLATE);
        MessageTemplate messageTemplate = new MessageTemplate();
        messageTemplate.setId(record.getId());
        messageTemplate.setName(record.getName());
        messageTemplate.setAlertType(record.getAlertType());
        messageTemplate.setSeverity(record.getSeverity());
        if (record.getChannelCode() != null) {
            Channel channel = channelService.getChannel(record.getChannelCode());
            messageTemplate.setChannel(channel);
        }
        messageTemplate.setCaption(record.getCaption());
        messageTemplate.setText(record.getText());
        messageTemplate.setFormationType(record.getFormationType());
        messageTemplate.setEnabled(record.getEnabled());
        messageTemplate.setCode(record.getCode());
        return messageTemplate;
    };


    /**
     * Получение страницы шаблонов уведомлений
     *
     * @param tenantCode Код тенанта
     * @param criteria   Критерии шаблонов уведомлений
     * @return Страница шаблонов уведомлений
     */
    public Page<MessageTemplate> getTemplates(String tenantCode, MessageTemplateCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(MESSAGE_TEMPLATE.TENANT_CODE.eq(tenantCode));
        Optional.ofNullable(criteria.getSeverity())
                .ifPresent(severity -> conditions.add(MESSAGE_TEMPLATE.SEVERITY.eq(severity)));
        Optional.ofNullable(criteria.getAlertType())
                .ifPresent(alertType -> conditions.add(MESSAGE_TEMPLATE.ALERT_TYPE.eq(alertType)));
        Optional.ofNullable(criteria.getChannelCode())
                .ifPresent(channelCode -> conditions.add(MESSAGE_TEMPLATE.CHANNEL_CODE.eq(channelCode)));
        Optional.ofNullable(criteria.getName()).filter(StringUtils::isNotBlank)
                .ifPresent(name -> conditions.add(MESSAGE_TEMPLATE.NAME.containsIgnoreCase(name)));
        Optional.ofNullable(criteria.getFormationType())
                .ifPresent(formationType -> conditions.add(MESSAGE_TEMPLATE.FORMATION_TYPE.eq(formationType)));
        Optional.ofNullable(criteria.getEnabled())
                .ifPresent(enabled -> conditions.add(MESSAGE_TEMPLATE.ENABLED.eq(enabled)));
        Optional.ofNullable(criteria.getCode()).filter(StringUtils::isNotBlank)
                .ifPresent(code -> conditions.add(MESSAGE_TEMPLATE.CODE.contains(code)));

        List<MessageTemplate> list = dsl
                .select(MESSAGE_TEMPLATE.fields())
                .from(MESSAGE_TEMPLATE)
                .where(conditions)
                .orderBy(getSortFields(criteria.getSort()))
                .limit(criteria.getPageSize())
                .offset((int) criteria.getOffset())
                .fetch()
                .map(MAPPER);

        Integer count = dsl
                .selectCount()
                .from(MESSAGE_TEMPLATE)
                .where(conditions)
                .fetchOne().component1();
        return new PageImpl<>(list, criteria, count);
    }

    /**
     * Создание шаблона уведомлений
     *
     * @param tenantCode      Код тенанта
     * @param messageTemplate Шаблон уведомлений
     * @return Созданный шаблон уведомлений
     */
    @Transactional
    public MessageTemplate createTemplate(String tenantCode, MessageTemplate messageTemplate) {
        Integer id = dsl.nextval(MESSAGE_TEMPLATE_ID_SEQ).intValue();
        messageTemplate.setId(id);

        dsl
                .insertInto(MESSAGE_TEMPLATE)
                .columns(MESSAGE_TEMPLATE.ID, MESSAGE_TEMPLATE.NAME,
                        MESSAGE_TEMPLATE.ALERT_TYPE, MESSAGE_TEMPLATE.SEVERITY, MESSAGE_TEMPLATE.CHANNEL_CODE,
                        MESSAGE_TEMPLATE.FORMATION_TYPE, MESSAGE_TEMPLATE.ENABLED,
                        MESSAGE_TEMPLATE.CAPTION, MESSAGE_TEMPLATE.TEXT,
                        MESSAGE_TEMPLATE.CODE, MESSAGE_TEMPLATE.TENANT_CODE
                )
                .values(id, messageTemplate.getName(),
                        messageTemplate.getAlertType(), messageTemplate.getSeverity(),
                        messageTemplate.getChannel() != null ? messageTemplate.getChannel().getId() : null,
                        messageTemplate.getFormationType(), messageTemplate.getEnabled(), messageTemplate.getCaption(), messageTemplate.getText(),
                        messageTemplate.getCode(), tenantCode)
                .execute();

        return messageTemplate;
    }

    /**
     * Обновление шаблона уведомлений
     *
     * @param id              Идентификатор шаблона уведомлений
     * @param messageTemplate Обновленный шаблон уведомлений
     */
    @Transactional
    public void updateTemplate(Integer id, MessageTemplate messageTemplate, String tenantCode) {
        dsl
                .update(MESSAGE_TEMPLATE)
                .set(MESSAGE_TEMPLATE.NAME, messageTemplate.getName())
                .set(MESSAGE_TEMPLATE.ALERT_TYPE, messageTemplate.getAlertType())
                .set(MESSAGE_TEMPLATE.SEVERITY, messageTemplate.getSeverity())
                .set(MESSAGE_TEMPLATE.CHANNEL_CODE, messageTemplate.getChannel() != null ? messageTemplate.getChannel().getId() : null)
                .set(MESSAGE_TEMPLATE.FORMATION_TYPE, messageTemplate.getFormationType())
                .set(MESSAGE_TEMPLATE.ENABLED, messageTemplate.getEnabled())
                .set(MESSAGE_TEMPLATE.CAPTION, messageTemplate.getCaption())
                .set(MESSAGE_TEMPLATE.TEXT, messageTemplate.getText())
                .set(MESSAGE_TEMPLATE.CODE, messageTemplate.getCode())
                .where(MESSAGE_TEMPLATE.ID.eq(id), MESSAGE_TEMPLATE.TENANT_CODE.eq(tenantCode))
                .execute();
    }

    /**
     * Удаление шаблона уведомлений
     *
     * @param id Идентификатор шаблона уведомлений
     */
    @Transactional
    public void deleteTemplate(Integer id, String tenantCode) {
        dsl
                .deleteFrom(MESSAGE_TEMPLATE)
                .where(MESSAGE_TEMPLATE.ID.eq(id), MESSAGE_TEMPLATE.TENANT_CODE.eq(tenantCode))
                .execute();
    }

    /**
     * Получение шаблона уведомления по идентификатору
     *
     * @param id Идентификатор шаблона уведомления
     * @return Шаблон уведомления
     */
    public MessageTemplate getTemplate(Integer id, String tenantCode) {
        MessageTemplateRecord messageTemplateRecord = dsl
                .selectFrom(MESSAGE_TEMPLATE)
                .where(MESSAGE_TEMPLATE.ID.eq(id), MESSAGE_TEMPLATE.TENANT_CODE.eq(tenantCode))
                .fetchOne();
        return messageTemplateRecord != null ? messageTemplateRecord.map(MAPPER) : null;
    }

    /**
     * Получение шаблона уведомления по коду
     *
     * @param code Код шаблона уведомления
     * @return Шаблон уведомления
     */
    public MessageTemplate getTemplate(String code, String tenantCode) {
        MessageTemplateRecord messageTemplateRecord = dsl
                .selectFrom(MESSAGE_TEMPLATE)
                .where(MESSAGE_TEMPLATE.CODE.eq(code), MESSAGE_TEMPLATE.TENANT_CODE.eq(tenantCode))
                .fetchOne();
        return messageTemplateRecord != null ? messageTemplateRecord.map(MAPPER) : null;
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
            Field field = MESSAGE_TEMPLATE.field(s.getProperty());
            return (SortField<?>) (s.getDirection().equals(Sort.Direction.ASC) ?
                    field.asc() : field.desc());
        }).collect(Collectors.toList());
    }
}
