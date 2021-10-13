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
import ru.inovus.messaging.api.criteria.MessageSettingCriteria;
import ru.inovus.messaging.api.model.Channel;
import ru.inovus.messaging.api.model.MessageSetting;
import ru.inovus.messaging.impl.jooq.tables.records.MessageSettingRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.inovus.messaging.impl.jooq.Sequences.MESSAGE_SETTING_ID_SEQ;
import static ru.inovus.messaging.impl.jooq.Tables.MESSAGE_SETTING;

@Service
public class MessageSettingService {

    @Autowired
    private ChannelService channelService;

    RecordMapper<Record, MessageSetting> MAPPER = rec -> {
        MessageSettingRecord settingRec = rec.into(MESSAGE_SETTING);
        MessageSetting messageSetting = new MessageSetting();
        messageSetting.setId(settingRec.getId());
        messageSetting.setName(settingRec.getName());
        messageSetting.setAlertType(settingRec.getAlertType());
        messageSetting.setSeverity(settingRec.getSeverity());
        if (settingRec.getChannelId() != null) {
            Channel channel = channelService.getChannel(settingRec.getChannelId());
            messageSetting.setChannel(channel);
        }
        messageSetting.setCaption(settingRec.getCaption());
        messageSetting.setText(settingRec.getText());
        messageSetting.setFormationType(settingRec.getFormationType());
        messageSetting.setDisabled(settingRec.getIsDisabled());
        messageSetting.setCode(settingRec.getCode());
        return messageSetting;
    };
    private final DSLContext dsl;

    public MessageSettingService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Page<MessageSetting> getSettings(String tenantCode, MessageSettingCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(MESSAGE_SETTING.TENANT_CODE.eq(tenantCode));
        Optional.ofNullable(criteria.getSeverity())
                .ifPresent(severity -> conditions.add(MESSAGE_SETTING.SEVERITY.eq(severity)));
        Optional.ofNullable(criteria.getAlertType())
                .ifPresent(alertType -> conditions.add(MESSAGE_SETTING.ALERT_TYPE.eq(alertType)));
        Optional.ofNullable(criteria.getChannelId())
                .ifPresent(channelId -> conditions.add(MESSAGE_SETTING.CHANNEL_ID.eq(channelId)));
        Optional.ofNullable(criteria.getName()).filter(StringUtils::isNotBlank)
                .ifPresent(name -> conditions.add(MESSAGE_SETTING.NAME.containsIgnoreCase(name)));
        Optional.ofNullable(criteria.getFormationType())
                .ifPresent(formationType -> conditions.add(MESSAGE_SETTING.FORMATION_TYPE.eq(formationType)));
        Optional.ofNullable(criteria.getEnabled())
                .ifPresent(enabled -> conditions.add(MESSAGE_SETTING.IS_DISABLED.notEqual(enabled)));
        Optional.ofNullable(criteria.getCode()).filter(StringUtils::isNotBlank)
                .ifPresent(code -> conditions.add(MESSAGE_SETTING.CODE.contains(code)));

        List<MessageSetting> list = dsl
                .select(MESSAGE_SETTING.fields())
                .from(MESSAGE_SETTING)
                .where(conditions)
                .orderBy(getSortFields(criteria.getSort()))
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

    @Transactional
    public void createSetting(String tenantCode, MessageSetting messageSetting) {
        Long id = dsl.nextval(MESSAGE_SETTING_ID_SEQ);
        dsl
                .insertInto(MESSAGE_SETTING)
                .columns(MESSAGE_SETTING.ID, MESSAGE_SETTING.NAME,
                        MESSAGE_SETTING.ALERT_TYPE, MESSAGE_SETTING.SEVERITY, MESSAGE_SETTING.CHANNEL_ID,
                        MESSAGE_SETTING.FORMATION_TYPE, MESSAGE_SETTING.IS_DISABLED,
                        MESSAGE_SETTING.CAPTION, MESSAGE_SETTING.TEXT,
                        MESSAGE_SETTING.CODE, MESSAGE_SETTING.TENANT_CODE
                )
                .values(id.intValue(), messageSetting.getName(),
                        messageSetting.getAlertType(), messageSetting.getSeverity(),
                        messageSetting.getChannel() != null ? messageSetting.getChannel().getId() : null,
                        messageSetting.getFormationType(), messageSetting.getDisabled(), messageSetting.getCaption(), messageSetting.getText(),
                        messageSetting.getCode(), tenantCode)
                .execute();
    }

    @Transactional
    public void updateSetting(Integer id, MessageSetting messageSetting) {
        dsl
                .update(MESSAGE_SETTING)
                .set(MESSAGE_SETTING.NAME, messageSetting.getName())
                .set(MESSAGE_SETTING.ALERT_TYPE, messageSetting.getAlertType())
                .set(MESSAGE_SETTING.SEVERITY, messageSetting.getSeverity())
                .set(MESSAGE_SETTING.CHANNEL_ID, messageSetting.getChannel() != null ? messageSetting.getChannel().getId() : null)
                .set(MESSAGE_SETTING.FORMATION_TYPE, messageSetting.getFormationType())
                .set(MESSAGE_SETTING.IS_DISABLED, messageSetting.getDisabled())
                .set(MESSAGE_SETTING.CAPTION, messageSetting.getCaption())
                .set(MESSAGE_SETTING.TEXT, messageSetting.getText())
                .set(MESSAGE_SETTING.CODE, messageSetting.getCode())
                .where(MESSAGE_SETTING.ID.eq(id))
                .execute();
    }

    @Transactional
    public void deleteSetting(Integer id) {
        dsl
                .deleteFrom(MESSAGE_SETTING)
                .where(MESSAGE_SETTING.ID.eq(id))
                .execute();
    }

    public MessageSetting getSetting(Integer id) {
        MessageSetting ms = dsl
                .selectFrom(MESSAGE_SETTING)
                .where(MESSAGE_SETTING.ID.eq(id))
                .fetchOne()
                .map(MAPPER);

        return ms;
    }

    public MessageSetting getSetting(String code) {
        MessageSetting ms = dsl
                .selectFrom(MESSAGE_SETTING)
                .where(MESSAGE_SETTING.CODE.eq(code))
                .fetchOne()
                .map(MAPPER);

        return ms;
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
