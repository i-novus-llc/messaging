package ru.inovus.messaging.impl.rest;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.MessageSettingCriteria;
import ru.inovus.messaging.api.model.MessageSetting;
import ru.inovus.messaging.api.rest.MessageSettingRest;
import ru.inovus.messaging.impl.service.MessageSettingService;

@Controller
public class MessageSettingRestImpl implements MessageSettingRest {

    private final MessageSettingService messageSettingService;

    public MessageSettingRestImpl(MessageSettingService messageSettingService) {
        this.messageSettingService = messageSettingService;
    }

    @Override
    public Page<MessageSetting> getSettings(String tenantCode, MessageSettingCriteria criteria) {
        return messageSettingService.getSettings(tenantCode, criteria);
    }

    @Override
    public void createSetting(String tenantCode, MessageSetting messageSetting) {
        messageSettingService.createSetting(tenantCode, messageSetting);
    }

    @Override
    public void updateSetting(String tenantCode, Integer id, MessageSetting messageSetting) {
        messageSettingService.updateSetting(id, messageSetting);
    }

    @Override
    public void deleteSetting(String tenantCode, Integer id) {
        messageSettingService.deleteSetting(id);
    }

    @Override
    public MessageSetting getSetting(String tenantCode, Integer id) {
        return messageSettingService.getSetting(id);
    }
}
