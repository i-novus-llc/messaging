package ru.inovus.messaging.impl.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.*;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.api.rest.MessageRest;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.impl.service.ChannelService;
import ru.inovus.messaging.impl.service.MessageService;
import ru.inovus.messaging.impl.service.MessageTemplateService;
import ru.inovus.messaging.impl.service.RecipientService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
public class MessageRestImpl implements MessageRest {
    private final MessageService messageService;
    private final MessageTemplateService messageTemplateService;
    private final RecipientService recipientService;
    private final ChannelService channelService;
    private final MqProvider mqProvider;

    public MessageRestImpl(MessageService messageService,
                           MessageTemplateService messageTemplateService,
                           RecipientService recipientService,
                           ChannelService channelService,
                           MqProvider mqProvider) {
        this.messageService = messageService;
        this.messageTemplateService = messageTemplateService;
        this.recipientService = recipientService;
        this.channelService = channelService;
        this.mqProvider = mqProvider;
    }

    @Override
    public Page<Message> getMessages(String tenantCode, MessageCriteria criteria) {
        return messageService.getMessages(tenantCode, criteria);
    }

    @Override
    public Message getMessage(String tenantCode, UUID id) {
        Message message = messageService.getMessage(id);
        recipientService.enrichRecipient(message.getRecipients());
        return message;
    }

    @Override
    public void sendMessage(String tenantCode, final MessageOutbox messageOutbox) {
        if (messageOutbox.getMessage() != null) {
            messageOutbox.getMessage().setTenantCode(tenantCode);
            recipientService.enrichRecipient(messageOutbox.getMessage().getRecipients());
            save(messageOutbox.getMessage());
            send(messageOutbox.getMessage());
        } else if (messageOutbox.getTemplateMessageOutbox() != null) {
            messageOutbox.getTemplateMessageOutbox().setTenantCode(tenantCode);
            buildAndSendMessage(messageOutbox.getTemplateMessageOutbox());
        }
    }

    /**
     * Построение уведомления по шаблону и отправка
     *
     * @param templateMessageOutbox Уведомление для построения по шаблону
     */
    private void buildAndSendMessage(TemplateMessageOutbox templateMessageOutbox) {
        MessageTemplate template = messageTemplateService.getTemplate(templateMessageOutbox.getTemplateCode());

        if (template == null || Boolean.FALSE.equals(template.getEnabled()))
            return;

        if (!CollectionUtils.isEmpty(templateMessageOutbox.getUserNameList())) {
            Message message = buildMessage(template, templateMessageOutbox.getUserNameList(), templateMessageOutbox);
            save(message);
            send(message);
        }
    }

    /**
     * Сохранение уведомления
     *
     * @param message Уведомление
     */
    private void save(Message message) {
        Message savedMessage = messageService.createMessage(message, message.getRecipients() == null ? null : message.getRecipients().toArray(new Recipient[0]));
        message.setId(savedMessage.getId());
    }

    /**
     * Отправка уведомления в канал отправки
     *
     * @param message Уведомление
     */
    private void send(Message message) {
        Channel channel = channelService.getChannel(message.getChannel().getId());
        mqProvider.publish(constructMessage(message), channel.getQueueName());
    }

    /**
     * Построение уведомления по шаблону
     *
     * @param messageTemplate Шаблон уведомления
     * @param userNameList    Список получателей
     * @param params          Параметры для построения по шаблону
     * @return Уведомление
     */
    private Message buildMessage(MessageTemplate messageTemplate, List<String> userNameList, TemplateMessageOutbox params) {
        Message message = new Message();
        message.setCaption(buildText(messageTemplate.getCaption(), params.getPlaceholders()));
        message.setText(buildText(messageTemplate.getText(), params.getPlaceholders()));
        message.setSeverity(messageTemplate.getSeverity());
        message.setAlertType(messageTemplate.getAlertType());
        message.setSentAt(params.getSentAt());
        message.setChannel(messageTemplate.getChannel());
        message.setFormationType(messageTemplate.getFormationType());
        message.setRecipientType(RecipientType.RECIPIENT);
        message.setTenantCode(params.getTenantCode());
        message.setRecipients(recipientService.getRecipientsByUsername(userNameList));
        message.setTemplateCode(params.getTemplateCode());
        return message;
    }

    /**
     * Замена плейсхолдеров во входном тексте
     *
     * @param text         Входной текст
     * @param placeholders Заменители плейсхолдеров
     * @return Текст с замененными плейсхолдерами
     */
    private String buildText(String text, Map<String, Object> placeholders) {
        for (Map.Entry<String, Object> placeHolder : placeholders.entrySet())
            text = text.replace(placeHolder.getKey(), placeHolder.getValue().toString());
        return text;
    }

    /**
     * Конструирование уведомления для публикации в очередь
     *
     * @param message Уведомление
     * @return
     */
    private Message constructMessage(Message message) {
        Message newMessage = new Message();
        newMessage.setId(message.getId());
        newMessage.setCaption(message.getCaption());
        newMessage.setText(message.getText());
        newMessage.setSeverity(message.getSeverity());
        newMessage.setRecipients(message.getRecipients());
        newMessage.setTenantCode(message.getTenantCode());
        return newMessage;
    }
}
