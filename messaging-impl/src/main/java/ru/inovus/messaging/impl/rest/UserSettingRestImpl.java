package ru.inovus.messaging.impl.rest;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ru.inovus.messaging.api.criteria.UserSettingCriteria;
import ru.inovus.messaging.api.model.Component;
import ru.inovus.messaging.api.model.InfoType;
import ru.inovus.messaging.api.model.UserSetting;
import ru.inovus.messaging.api.rest.UserSettingRest;
import ru.inovus.messaging.impl.jooq.tables.records.MessageSettingRecord;
import ru.inovus.messaging.impl.jooq.tables.records.UserSettingRecord;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.inovus.messaging.impl.jooq.Tables.*;

@Controller
public class UserSettingRestImpl implements UserSettingRest {

    @Autowired
    private DSLContext dsl;

    private RecordMapper<Record, UserSetting> MAPPER = record -> {
        MessageSettingRecord defaultSetting = record.into(MESSAGE_SETTING);
        UserSettingRecord userSetting = record.into(USER_SETTING);
        UserSetting setting = new UserSetting();
        setting.setId(defaultSetting.getId());
        setting.setCaption(defaultSetting.getCaption());
        setting.setText(defaultSetting.getText());
        setting.setSeverity(defaultSetting.getSeverity());
        setting.setName(defaultSetting.getName());
        setting.setComponent(defaultSetting.getComponentId() != null ?
                new Component(defaultSetting.getComponentId(), record.into(COMPONENT).getName()) : null);
        setting.setDisabled(userSetting.getIsDisabled() != null ?
                userSetting.getIsDisabled() : defaultSetting.getIsDisabled());
        setting.setAlertType(userSetting.getAlertType() != null ?
                userSetting.getAlertType() : defaultSetting.getAlertType());
        setting.setDefaultAlertType(defaultSetting.getAlertType());
        List<InfoType> defInfoTypes = getInfoTypes(defaultSetting.getSendNotice(), defaultSetting.getSendEmail());
        setting.setDefaultChannelType(defInfoTypes);
        if (userSetting.getSendNotice() == null && userSetting.getSendEmail() == null) {
            setting.setChannelType(defInfoTypes);
        } else {
            setting.setChannelType(getInfoTypes(userSetting.getSendNotice(), userSetting.getSendEmail()));
        }
        setting.setTemplateCode(defaultSetting.getCode());
        setting.setDefaultSetting(userSetting.getId() == null);
        return setting;
    };

    private List<InfoType> getInfoTypes(Boolean sendNotice, Boolean sendEmail) {
        List<InfoType> infoTypes = new ArrayList<>();
        if (sendNotice != null && sendNotice) {
            infoTypes.add(InfoType.NOTICE);
        }
        if (sendEmail != null && sendEmail) {
            infoTypes.add(InfoType.EMAIL);
        }
        return infoTypes;
    }

    @Override
    public Page<UserSetting> getSettings(UserSettingCriteria criteria) {
        List<Condition> conditions = new ArrayList<>();
        Optional.ofNullable(criteria.getComponentId())
            .ifPresent(componentId -> conditions.add(MESSAGE_SETTING.COMPONENT_ID.eq(componentId)));
        Optional.ofNullable(criteria.getSeverity())
            .ifPresent(severity -> conditions.add(MESSAGE_SETTING.SEVERITY.eq(severity)));
        Optional.ofNullable(criteria.getName()).filter(StringUtils::isNotBlank)
            .ifPresent(name -> conditions.add(MESSAGE_SETTING.NAME.containsIgnoreCase(name)));
        Optional.ofNullable(criteria.getAlertType())
            .ifPresent(alertType -> conditions.add(
                USER_SETTING.ALERT_TYPE.isNotNull()
                    .and(USER_SETTING.ALERT_TYPE.eq(alertType))
                    .or(USER_SETTING.ALERT_TYPE.isNull().and(MESSAGE_SETTING.ALERT_TYPE.eq(alertType)))));
        if (InfoType.EMAIL.equals(criteria.getChannelType())) {
            Optional.ofNullable(criteria.getChannelType())
                .ifPresent(infoType -> conditions.add(
                    USER_SETTING.SEND_EMAIL.isNotNull().and(USER_SETTING.SEND_EMAIL.isTrue())
                        .or(USER_SETTING.SEND_EMAIL.isNull().and(MESSAGE_SETTING.SEND_EMAIL.isTrue()))));
        }
        if (InfoType.NOTICE.equals(criteria.getChannelType())) {
            Optional.ofNullable(criteria.getChannelType())
                .ifPresent(infoType -> conditions.add(
                    USER_SETTING.SEND_NOTICE.isNotNull().and(USER_SETTING.SEND_NOTICE.isTrue())
                        .or(USER_SETTING.SEND_NOTICE.isNull().and(MESSAGE_SETTING.SEND_NOTICE.isTrue()))));
        }
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
            .select(COMPONENT.fields())
            .from(MESSAGE_SETTING)
            .leftJoin(USER_SETTING).on(USER_SETTING.MSG_SETTING_ID.eq(MESSAGE_SETTING.ID),
                USER_SETTING.USER_ID.eq(criteria.getUser())) // in fact it's username
            .leftJoin(COMPONENT).on(MESSAGE_SETTING.COMPONENT_ID.eq(COMPONENT.ID))
            .where(conditions)
            .limit(criteria.getPageSize())
            .offset((int) criteria.getOffset())
            .fetch()
            .map(MAPPER);
        Integer count = dsl
            .selectCount()
            .from(MESSAGE_SETTING)
            .leftJoin(USER_SETTING).on(USER_SETTING.MSG_SETTING_ID.eq(MESSAGE_SETTING.ID),
                USER_SETTING.USER_ID.eq(criteria.getUser()))
//                .leftJoin(COMPONENT).on(MESSAGE_SETTING.COMPONENT_ID.eq(COMPONENT.ID))//maybe drop this join? not in where clause anyway
            .where(conditions)
            .fetchOne()
            .component1();
        return new PageImpl<>(list, criteria, (long) count);
    }

    @Override
    public UserSetting getSetting(String user, Integer id) {
        Record record = dsl
            .select(MESSAGE_SETTING.fields())
            .select(USER_SETTING.fields())
            .select(COMPONENT.fields())
            .from(MESSAGE_SETTING)
            .leftJoin(USER_SETTING).on(USER_SETTING.MSG_SETTING_ID.eq(MESSAGE_SETTING.ID),
                USER_SETTING.USER_ID.eq(user))
            .leftJoin(COMPONENT).on(MESSAGE_SETTING.COMPONENT_ID.eq(COMPONENT.ID))
            .where(MESSAGE_SETTING.ID.eq(id))
            .fetchOne();

        if (record == null)
            throw new NotFoundException("User setting doesn't exists");

        return record.map(MAPPER);
    }

    @Override
    @Transactional
    public void updateSetting(String user, Integer msgSettingId, UserSetting setting) {
        //1. Проверяем существует ли уже в таблице public.user_setting для переданного пользователя (user) для шаблона уведомления (msgSettingId) своя запись
        boolean userSettingExists = dsl
            .selectCount()
            .from(USER_SETTING)
            .where(USER_SETTING.MSG_SETTING_ID.eq(msgSettingId))
            .and(USER_SETTING.USER_ID.eq(user))
            .fetchOne()
            .component1() != 0;

        boolean messageSettingsExists;

        // Если в таблице public.user_setting уже ЕСТЬ такая запись , то просто ее обновляем
        if (userSettingExists) {

            dsl
                .update(USER_SETTING)
                .set(USER_SETTING.ALERT_TYPE, setting.getAlertType())
                .set(USER_SETTING.SEND_NOTICE, setting.getChannelType() != null && setting.getChannelType().contains(InfoType.NOTICE))
                .set(USER_SETTING.SEND_EMAIL, setting.getChannelType() != null && setting.getChannelType().contains(InfoType.EMAIL))
                .set(USER_SETTING.IS_DISABLED, setting.getDisabled())
                .where(USER_SETTING.MSG_SETTING_ID.eq(msgSettingId))
                .and(USER_SETTING.USER_ID.eq(user))
                .execute();
        } else {
//          Если в таблице public.user_setting такой записи НЕТ, то проверяем, есть ли с таким иден-ром запись в обшей таблице с шаблонами уведомлений public.message_setting
            messageSettingsExists = dsl
                .selectCount()
                .from(MESSAGE_SETTING)
                .where(MESSAGE_SETTING.ID.eq(msgSettingId))
                .fetchOne()
                .component1() != 0;

            if (!messageSettingsExists)
                throw new NotFoundException("Message setting doesn't exists");

//           Если в таблице public.message_setting запись с переданным иден-ром есть, но со ссылкой на нее и указанными настройками пользователя создаем запись в таблице public.user_setting
            dsl
                .insertInto(USER_SETTING)
                .columns(USER_SETTING.USER_ID, USER_SETTING.ALERT_TYPE, USER_SETTING.SEND_NOTICE,
                    USER_SETTING.SEND_EMAIL, USER_SETTING.IS_DISABLED, USER_SETTING.MSG_SETTING_ID)
                .values(
                    user,
                    setting.getAlertType(),
                    setting.getChannelType() != null && setting.getChannelType().contains(InfoType.NOTICE),
                    setting.getChannelType() != null && setting.getChannelType().contains(InfoType.EMAIL),
                    setting.getDisabled(),
                    msgSettingId
                )
                .execute();
        }
    }

}
