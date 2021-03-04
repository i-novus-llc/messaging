package ru.inovus.messaging.server.rest;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.MessageSetting;
import ru.inovus.messaging.api.criteria.MessageSettingCriteria;
import ru.inovus.messaging.api.rest.MessageSettingRest;
import ru.inovus.messaging.impl.service.MessageSettingService;

@Controller
public class MessageSettingRestImpl implements MessageSettingRest {

    private final MessageSettingService messageSettingService;

    public MessageSettingRestImpl(MessageSettingService messageSettingService) {
        this.messageSettingService = messageSettingService;
    }

    @Override
    public Page<MessageSetting> getSettings(MessageSettingCriteria criteria) {
        return messageSettingService.getSettings(criteria);
    }

    @Override
    public void createSetting(MessageSetting messageSetting) {
        messageSettingService.createSetting(messageSetting);
    }

    @Override
    public void updateSetting(Integer id, MessageSetting messageSetting) {
        messageSettingService.updateSetting(id, messageSetting);
    }

    @Override
    public void deleteSetting(Integer id) {
        messageSettingService.deleteSetting(id);
    }

    @Override
    public MessageSetting getSetting(Integer id) {
        return messageSettingService.getSetting(id);
    }
}
