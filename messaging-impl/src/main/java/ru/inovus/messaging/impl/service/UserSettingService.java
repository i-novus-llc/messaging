package ru.inovus.messaging.impl.service;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inovus.messaging.api.criteria.UserSettingCriteria;
import ru.inovus.messaging.api.model.UserSetting;
import ru.inovus.messaging.impl.jooq.tables.records.MessageSettingRecord;
import ru.inovus.messaging.impl.jooq.tables.records.UserSettingRecord;

import javax.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.inovus.messaging.impl.jooq.Tables.MESSAGE_SETTING;
import static ru.inovus.messaging.impl.jooq.Tables.USER_SETTING;

@Service
public class UserSettingService {
    @Autowired
    private DSLContext dsl;

    @Autowired
    private ChannelService channelService;

    private RecordMapper<Record, UserSetting> MAPPER = record -> {
        MessageSettingRecord defaultSetting = record.into(MESSAGE_SETTING);
        UserSettingRecord userSetting = record.into(USER_SETTING);
        UserSetting setting = new UserSetting();
        setting.setId(defaultSetting.getId());
        setting.setCaption(defaultSetting.getCaption());
        setting.setText(defaultSetting.getText());
        setting.setSeverity(defaultSetting.getSeverity());
        setting.setName(defaultSetting.getName());
        setting.setDisabled(userSetting.getIsDisabled() != null ?
                userSetting.getIsDisabled() : defaultSetting.getIsDisabled());
        setting.setAlertType(userSetting.getAlertType() != null ?
                userSetting.getAlertType() : defaultSetting.getAlertType());
        setting.setDefaultAlertType(defaultSetting.getAlertType());

        setting.setChannel(channelService.getChannel(
                userSetting.getChannelId() != null ? userSetting.getChannelId() : defaultSetting.getChannelId()
        ));

        setting.setTemplateCode(defaultSetting.getCode());
        setting.setDefaultSetting(userSetting.getId() == null);
        return setting;
    };

    public Page<UserSetting> getSettings(String tenantCode, UserSettingCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(USER_SETTING.TENANT_CODE.eq(tenantCode));
        conditions.add(MESSAGE_SETTING.TENANT_CODE.eq(tenantCode));
        Optional.ofNullable(criteria.getSeverity())
                .ifPresent(severity -> conditions.add(MESSAGE_SETTING.SEVERITY.eq(severity)));
        Optional.ofNullable(criteria.getName()).filter(StringUtils::isNotBlank)
                .ifPresent(name -> conditions.add(MESSAGE_SETTING.NAME.containsIgnoreCase(name)));
        Optional.ofNullable(criteria.getAlertType())
                .ifPresent(alertType -> conditions.add(
                        USER_SETTING.ALERT_TYPE.isNotNull()
                                .and(USER_SETTING.ALERT_TYPE.eq(alertType))
                                .or(USER_SETTING.ALERT_TYPE.isNull().and(MESSAGE_SETTING.ALERT_TYPE.eq(alertType)))));
        Optional.ofNullable(criteria.getChannelId())
                .ifPresent(channelId -> conditions.add(
                        USER_SETTING.CHANNEL_ID.eq(channelId)
                                .or(MESSAGE_SETTING.CHANNEL_ID.eq(channelId))));
        conditions.add(MESSAGE_SETTING.IS_DISABLED.isFalse());
        Optional.ofNullable(criteria.getEnabled())
                .ifPresent(enabled -> conditions.add(USER_SETTING.IS_DISABLED.isNull().and(DSL.value(enabled))
                        .or(USER_SETTING.IS_DISABLED.notEqual(enabled))));
        Optional.ofNullable(criteria.getEnabled())
                .ifPresent(enabled -> conditions.add(
                        MESSAGE_SETTING.IS_DISABLED.isFalse() // user shouldn't see settings disabled by admin
                                .and(USER_SETTING.IS_DISABLED.isNull().and(DSL.value(enabled))
                                        .or(USER_SETTING.IS_DISABLED.notEqual(enabled)))));
        Optional.ofNullable(criteria.getTemplateCode()).filter(StringUtils::isNotBlank)
                .ifPresent(templateCode -> conditions.add(MESSAGE_SETTING.CODE.containsIgnoreCase(templateCode)));

        List<UserSetting> list = dsl
                .select(MESSAGE_SETTING.fields())
                .select(USER_SETTING.fields())
                .from(MESSAGE_SETTING)
                .leftJoin(USER_SETTING).on(USER_SETTING.MSG_SETTING_ID.eq(MESSAGE_SETTING.ID),
                        USER_SETTING.USER_ID.eq(criteria.getUsername()))
                .where(conditions)
                .limit(criteria.getPageSize())
                .offset((int) criteria.getOffset())
                .fetch()
                .map(MAPPER);
        Integer count = dsl
                .selectCount()
                .from(MESSAGE_SETTING)
                .leftJoin(USER_SETTING).on(USER_SETTING.MSG_SETTING_ID.eq(MESSAGE_SETTING.ID),
                        USER_SETTING.USER_ID.eq(criteria.getUsername()))
                .where(conditions)
                .fetchOne()
                .component1();
        return new PageImpl<>(list, criteria, (long) count);
    }

    public UserSetting getSetting(String username, Integer id) {
        Record record = dsl
                .select(MESSAGE_SETTING.fields())
                .select(USER_SETTING.fields())
                .from(MESSAGE_SETTING)
                .leftJoin(USER_SETTING).on(USER_SETTING.MSG_SETTING_ID.eq(MESSAGE_SETTING.ID),
                        USER_SETTING.USER_ID.eq(username))
                .where(MESSAGE_SETTING.ID.eq(id))
                .fetchOne();

        if (record == null)
            throw new NotFoundException("User setting doesn't exists");

        return record.map(MAPPER);
    }

    @Transactional
    public void updateSetting(String tenantCode, String username,
                              Integer msgSettingId, UserSetting setting) {
        //1. Проверяем существует ли уже в таблице user_setting для переданного пользователя (username) для шаблона уведомления (msgSettingId) своя запись
        boolean userSettingExists = dsl
                .selectCount()
                .from(USER_SETTING)
                .where(USER_SETTING.MSG_SETTING_ID.eq(msgSettingId))
                .and(USER_SETTING.USER_ID.eq(username))
                .and(USER_SETTING.TENANT_CODE.eq(tenantCode))
                .fetchOne()
                .component1() != 0;

        boolean messageSettingsExists;

        // Если в таблице user_setting уже ЕСТЬ такая запись , то просто ее обновляем
        if (userSettingExists) {

            dsl
                    .update(USER_SETTING)
                    .set(USER_SETTING.ALERT_TYPE, setting.getAlertType())
                    .set(USER_SETTING.CHANNEL_ID, setting.getChannel() != null ? setting.getChannel().getId() : null)
                    .set(USER_SETTING.IS_DISABLED, setting.getDisabled())
                    .where(USER_SETTING.MSG_SETTING_ID.eq(msgSettingId))
                    .and(USER_SETTING.USER_ID.eq(username))
                    .execute();
        } else {
//          Если в таблице user_setting такой записи НЕТ, то проверяем, есть ли с таким иден-ром запись в обшей таблице с шаблонами уведомлений message_setting
            messageSettingsExists = dsl
                    .selectCount()
                    .from(MESSAGE_SETTING)
                    .where(MESSAGE_SETTING.ID.eq(msgSettingId))
                    .fetchOne()
                    .component1() != 0;

            if (!messageSettingsExists)
                throw new NotFoundException("Message setting doesn't exists");

//           Если в таблице message_setting запись с переданным иден-ром есть, но со ссылкой на нее и указанными настройками пользователя создаем запись в таблице user_setting
            dsl
                    .insertInto(USER_SETTING)
                    .columns(USER_SETTING.USER_ID, USER_SETTING.ALERT_TYPE, USER_SETTING.CHANNEL_ID,
                            USER_SETTING.IS_DISABLED, USER_SETTING.MSG_SETTING_ID, USER_SETTING.TENANT_CODE)
                    .values(
                            username,
                            setting.getAlertType(),
                            setting.getChannel() != null ? setting.getChannel().getId() : null,
                            setting.getDisabled(),
                            msgSettingId, tenantCode
                    )
                    .execute();
        }
    }
}
