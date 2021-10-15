package ru.inovus.messaging.impl.rest;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.UserSettingCriteria;
import ru.inovus.messaging.api.model.UserSetting;
import ru.inovus.messaging.api.rest.UserSettingRest;
import ru.inovus.messaging.impl.service.UserSettingService;

@Controller
public class UserSettingRestImpl implements UserSettingRest {

    private final UserSettingService userSettingService;

    public UserSettingRestImpl(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }

    @Override
    public Page<UserSetting> getSettings(String tenantCode, UserSettingCriteria criteria) {
        return userSettingService.getSettings(tenantCode, criteria);
    }

    @Override
    public UserSetting getSetting(String tenantCode, String username, Integer id) {
        return userSettingService.getSetting(username, id);
    }

    @Override
    public void updateSetting(String tenantCode, String username, Integer id, UserSetting messageSetting) {
        userSettingService.updateSetting(tenantCode, username, id, messageSetting);
    }
}
