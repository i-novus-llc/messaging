package ru.inovus.messaging.server.rest;

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
import ru.inovus.messaging.api.model.Component;
import ru.inovus.messaging.api.model.InfoType;
import ru.inovus.messaging.api.model.UserSetting;
import ru.inovus.messaging.api.criteria.UserSettingCriteria;
import ru.inovus.messaging.api.rest.UserSettingRest;
import ru.inovus.messaging.impl.jooq.tables.records.MessageSettingRecord;
import ru.inovus.messaging.impl.jooq.tables.records.UserSettingRecord;

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
        List<InfoType> infoTypes = getInfoTypes(userSetting.getSendNotice(), userSetting.getSendEmail());
        setting.setInfoTypes(infoTypes);
        setting.setDefaultAlertType(defaultSetting.getAlertType());
        List<InfoType> defInfoTypes = getInfoTypes(defaultSetting.getSendNotice(), defaultSetting.getSendEmail());
        setting.setDefaultInfoType(defInfoTypes);
        return setting;
    };

    private List<InfoType> getInfoTypes(Boolean sendNotice, Boolean sendEmail) {
        List<InfoType> infoTypes = new ArrayList<>();
        if (sendNotice != null) {
            infoTypes.add(InfoType.NOTICE);
        }
        if (sendEmail != null) {
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
        if (InfoType.EMAIL.equals(criteria.getInfoType())) {
            Optional.ofNullable(criteria.getInfoType())
                    .ifPresent(infoType -> conditions.add(USER_SETTING.SEND_EMAIL.isTrue()));
        }
        if (InfoType.NOTICE.equals(criteria.getInfoType())) {
            Optional.ofNullable(criteria.getInfoType())
                    .ifPresent(infoType -> conditions.add(USER_SETTING.SEND_NOTICE.isTrue()));
        }
        Optional.ofNullable(criteria.getEnabled())
                .ifPresent(enabled -> conditions.add(
                        MESSAGE_SETTING.IS_DISABLED.isFalse() // user shouldn't see settings disabled by admin
                                .and(USER_SETTING.IS_DISABLED.isNull().and(DSL.value(enabled))
                                        .or(USER_SETTING.IS_DISABLED.notEqual(enabled)))));
        List<UserSetting> list = dsl
                .select(MESSAGE_SETTING.fields())
                .select(USER_SETTING.fields())
                .from(MESSAGE_SETTING)
                .leftJoin(USER_SETTING).on(USER_SETTING.ID.eq(MESSAGE_SETTING.ID),
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
                .leftJoin(USER_SETTING).on(USER_SETTING.ID.eq(MESSAGE_SETTING.ID),
                        USER_SETTING.USER_ID.eq(criteria.getUser()))
//                .leftJoin(COMPONENT).on(MESSAGE_SETTING.COMPONENT_ID.eq(COMPONENT.ID))//maybe drop this join? not in where clause anyway
                .where(conditions)
                .fetchOne()
                .component1();
        return new PageImpl<>(list, criteria, (long) count);
    }

    @Override
    public UserSetting getSetting(String user, Integer id) {
        return dsl
                .select(MESSAGE_SETTING.fields())
                .select(USER_SETTING.fields())
                .from(MESSAGE_SETTING)
                .leftJoin(USER_SETTING).on(USER_SETTING.ID.eq(MESSAGE_SETTING.ID),
                        USER_SETTING.USER_ID.eq(user))
                .leftJoin(COMPONENT).on(MESSAGE_SETTING.COMPONENT_ID.eq(COMPONENT.ID))
                .where(MESSAGE_SETTING.ID.eq(id))
                .fetchOne()
                .map(MAPPER);
    }

    @Override
    @Transactional
    public void updateSetting(String user, Integer id, UserSetting setting) {
        boolean exists = dsl
                .selectCount()
                .from(USER_SETTING)
                .where(USER_SETTING.ID.eq(id))
                .fetchOne()
                .component1() != 0;
        if (exists) {
            dsl
                    .update(USER_SETTING)
                    .set(USER_SETTING.ALERT_TYPE, setting.getAlertType())
                    .set(USER_SETTING.SEND_NOTICE, setting.getInfoTypes() != null && setting.getInfoTypes().contains(InfoType.NOTICE))
                    .set(USER_SETTING.SEND_EMAIL, setting.getInfoTypes() != null && setting.getInfoTypes().contains(InfoType.EMAIL))
                    .set(USER_SETTING.IS_DISABLED, setting.getDisabled())
                    .where(USER_SETTING.ID.eq(id))
                    .execute();
        } else {
            dsl
                    .insertInto(USER_SETTING)
                    .set(USER_SETTING.ID, id)
                    .set(USER_SETTING.USER_ID, user)
                    .set(USER_SETTING.ALERT_TYPE, setting.getAlertType())
                    .set(USER_SETTING.SEND_NOTICE, setting.getInfoTypes() != null && setting.getInfoTypes().contains(InfoType.NOTICE))
                    .set(USER_SETTING.SEND_EMAIL, setting.getInfoTypes() != null && setting.getInfoTypes().contains(InfoType.EMAIL))
                    .set(USER_SETTING.IS_DISABLED, setting.getDisabled())
                    .execute();
        }
    }

}
