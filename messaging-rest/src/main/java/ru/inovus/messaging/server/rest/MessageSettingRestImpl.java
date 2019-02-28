package ru.inovus.messaging.server.rest;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ru.inovus.messaging.api.Component;
import ru.inovus.messaging.api.MessageSetting;
import ru.inovus.messaging.api.MessageSettingCriteria;
import ru.inovus.messaging.api.MessageSettingRest;
import ru.inovus.messaging.impl.jooq.tables.records.MessageSettingRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.inovus.messaging.impl.jooq.Sequences.MESSAGE_SETTING_ID_SEQ;
import static ru.inovus.messaging.impl.jooq.Tables.COMPONENT;
import static ru.inovus.messaging.impl.jooq.Tables.MESSAGE_SETTING;

@Controller
public class MessageSettingRestImpl implements MessageSettingRest {

    @Autowired
    private DSLContext dsl;

    RecordMapper<Record, MessageSetting> MAPPER = rec -> {
        MessageSettingRecord r = rec.into(MESSAGE_SETTING);
        MessageSetting messageSetting = new MessageSetting();
        messageSetting.setId(r.getId());
        messageSetting.setName(r.getName());
        messageSetting.setAlertType(r.getAlertType());
        messageSetting.setSeverity(r.getSeverity());
        messageSetting.setInfoType(r.getInfoType());
        messageSetting.setCaption(r.getCaption());
        messageSetting.setText(r.getText());
        messageSetting.setComponent(new Component(r.getComponentId(), rec.into(COMPONENT).getName()));
        messageSetting.setFormationType(r.getFormationType());
        messageSetting.setDisabled(r.getIsDisabled());
        return messageSetting;
    };

    @Override
    public Page<MessageSetting> getSettings(MessageSettingCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        Optional.ofNullable(criteria.getComponentId())
                .ifPresent(componentId -> conditions.add(MESSAGE_SETTING.COMPONENT_ID.eq(componentId)));
        Optional.ofNullable(criteria.getSeverity())
                .ifPresent(severity -> conditions.add(MESSAGE_SETTING.SEVERITY.eq(severity)));
        Optional.ofNullable(criteria.getAlertType())
                .ifPresent(alertType -> conditions.add(MESSAGE_SETTING.ALERT_TYPE.eq(alertType)));
        Optional.ofNullable(criteria.getInfoType())
                .ifPresent(infoType -> conditions.add(MESSAGE_SETTING.INFO_TYPE.eq(infoType)));
        Optional.ofNullable(criteria.getName()).filter(StringUtils::isNotBlank)
                .ifPresent(name -> conditions.add(MESSAGE_SETTING.NAME.containsIgnoreCase(name)));
        Optional.ofNullable(criteria.getFormationType())
                .ifPresent(formationType -> conditions.add(MESSAGE_SETTING.FORMATION_TYPE.eq(formationType)));
        Optional.ofNullable(criteria.getEnabled())
                .ifPresent(enabled -> conditions.add(MESSAGE_SETTING.IS_DISABLED.notEqual(enabled)));
        List<MessageSetting> list = dsl
                .select(MESSAGE_SETTING.fields())
                .select(COMPONENT.fields())
                .from(MESSAGE_SETTING)
                .leftJoin(COMPONENT).on(MESSAGE_SETTING.COMPONENT_ID.eq(COMPONENT.ID))
                .where(conditions)
                .limit(criteria.getPageSize())
                .offset((int) criteria.getOffset())
                .fetch()
                .map(MAPPER);
        Integer count = dsl
                .selectCount()
                .from(MESSAGE_SETTING)
                .where(conditions)
                .fetchOne().component1();
        return new PageImpl<>(list, criteria, count);
    }

    @Override
    @Transactional
    public void createSetting(MessageSetting messageSetting) {
        Long id = dsl.nextval(MESSAGE_SETTING_ID_SEQ);
        dsl
                .insertInto(MESSAGE_SETTING)
                .columns(MESSAGE_SETTING.ID, MESSAGE_SETTING.NAME, MESSAGE_SETTING.COMPONENT_ID,
                         MESSAGE_SETTING.ALERT_TYPE, MESSAGE_SETTING.SEVERITY, MESSAGE_SETTING.INFO_TYPE,
                         MESSAGE_SETTING.FORMATION_TYPE, MESSAGE_SETTING.IS_DISABLED,
                         MESSAGE_SETTING.CAPTION, MESSAGE_SETTING.TEXT)
                .values(id.intValue(), messageSetting.getName(), messageSetting.getComponent() != null ? messageSetting.getComponent().getId() : null,
                        messageSetting.getAlertType(), messageSetting.getSeverity(), messageSetting.getInfoType(),
                        messageSetting.getFormationType(), messageSetting.getDisabled(), messageSetting.getCaption(), messageSetting.getText())
                .execute();
    }

    @Override
    @Transactional
    public void updateSetting(Integer id, MessageSetting messageSetting) {
        dsl
                .update(MESSAGE_SETTING)
                .set(MESSAGE_SETTING.NAME, messageSetting.getName())
                .set(MESSAGE_SETTING.COMPONENT_ID, messageSetting.getComponent() != null ? messageSetting.getComponent().getId() : null)
                .set(MESSAGE_SETTING.ALERT_TYPE, messageSetting.getAlertType())
                .set(MESSAGE_SETTING.SEVERITY, messageSetting.getSeverity())
                .set(MESSAGE_SETTING.INFO_TYPE, messageSetting.getInfoType())
                .set(MESSAGE_SETTING.FORMATION_TYPE, messageSetting.getFormationType())
                .set(MESSAGE_SETTING.IS_DISABLED, messageSetting.getDisabled())
                .set(MESSAGE_SETTING.CAPTION, messageSetting.getCaption())
                .set(MESSAGE_SETTING.TEXT, messageSetting.getText())
                .where(MESSAGE_SETTING.ID.eq(id))
                .execute();
    }

    @Override
    @Transactional
    public void deleteSetting(Integer id) {
        dsl
                .deleteFrom(MESSAGE_SETTING)
                .where(MESSAGE_SETTING.ID.eq(id))
                .execute();
    }

    @Override
    public MessageSetting getSetting(Integer id) {
        return dsl
                .selectFrom(MESSAGE_SETTING)
                .where(MESSAGE_SETTING.ID.eq(id))
                .fetchOne()
                .map(MAPPER);
    }
}
