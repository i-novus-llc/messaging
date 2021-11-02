package ru.inovus.messaging.impl.rest;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.MessageTemplateCriteria;
import ru.inovus.messaging.api.model.MessageTemplate;
import ru.inovus.messaging.api.rest.MessageTemplateRest;
import ru.inovus.messaging.impl.service.MessageTemplateService;

@Controller
public class MessageTemplateRestImpl implements MessageTemplateRest {

    private final MessageTemplateService messageTemplateService;

    public MessageTemplateRestImpl(MessageTemplateService messageTemplateService) {
        this.messageTemplateService = messageTemplateService;
    }

    @Override
    public Page<MessageTemplate> getTemplates(String tenantCode, MessageTemplateCriteria criteria) {
        return messageTemplateService.getTemplates(tenantCode, criteria);
    }

    @Override
    public void createTemplate(String tenantCode, MessageTemplate messageTemplate) {
        messageTemplateService.createTemplate(tenantCode, messageTemplate);
    }

    @Override
    public void updateTemplate(String tenantCode, Integer id, MessageTemplate messageTemplate) {
        messageTemplateService.updateTemplate(tenantCode, id, messageTemplate);
    }

    @Override
    public void deleteTemplate(String tenantCode, Integer id) {
        messageTemplateService.deleteTemplate(tenantCode, id);
    }

    @Override
    public MessageTemplate getTemplate(String tenantCode, Integer id) {
        return messageTemplateService.getTemplate(tenantCode, id);
    }
}
