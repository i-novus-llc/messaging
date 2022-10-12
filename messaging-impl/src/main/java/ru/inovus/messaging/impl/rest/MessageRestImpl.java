package ru.inovus.messaging.impl.rest;

import lombok.extern.slf4j.Slf4j;
import net.n2oapp.platform.i18n.UserException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import ru.inovus.messaging.api.MessageAttachment;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.*;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.api.rest.MessageRest;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.impl.service.ChannelService;
import ru.inovus.messaging.impl.service.MessageService;
import ru.inovus.messaging.impl.service.MessageTemplateService;
import ru.inovus.messaging.impl.service.RecipientService;
import ru.inovus.messaging.impl.util.MessageHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Controller
public class MessageRestImpl implements MessageRest {
    private final MessageService messageService;
    private final MessageTemplateService messageTemplateService;
    private final RecipientService recipientService;
    private final ChannelService channelService;
    private final MqProvider mqProvider;
    private final MessageHelper messageHelper;
    private MessageAttachment attachmentService;

    public MessageRestImpl(MessageService messageService,
                           MessageTemplateService messageTemplateService,
                           RecipientService recipientService,
                           ChannelService channelService,
                           MqProvider mqProvider,
                           MessageHelper messageHelper,
                           @Nullable MessageAttachment attachmentService) {
        this.messageService = messageService;
        this.messageTemplateService = messageTemplateService;
        this.recipientService = recipientService;
        this.channelService = channelService;
        this.mqProvider = mqProvider;
        this.messageHelper = messageHelper;
        this.attachmentService = attachmentService;
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
            setRecipientsAndAttachments(tenantCode, messageOutbox);
            save(messageOutbox.getMessage());
            send(messageOutbox.getMessage());
        } else if (messageOutbox.getTemplateMessageOutbox() != null) {
            messageOutbox.getTemplateMessageOutbox().setTenantCode(tenantCode);
            buildAndSendMessage(messageOutbox.getTemplateMessageOutbox());
        }
    }

    /**
     * Выбор источника получателей, вложений и обогащение
     *
     * @param messageOutbox Уведомление
     */
    private void setRecipientsAndAttachments(String tenantCode, MessageOutbox messageOutbox) {
        Message newMessage = messageOutbox.getMessage();
        if (nonNull(newMessage.getId())) {
            Message oldMessage = getMessage(tenantCode, UUID.fromString(newMessage.getId()));
            if (!CollectionUtils.isEmpty(oldMessage.getRecipients())) {
                newMessage.setRecipients(oldMessage.getRecipients());
            }
            if (!CollectionUtils.isEmpty(oldMessage.getAttachments())) {
                newMessage.setAttachments(oldMessage.getAttachments().stream()
                        .peek(message -> message.setId(null))
                        .collect(Collectors.toList()));
            }
        }
        if (!CollectionUtils.isEmpty(newMessage.getRecipients()))
            recipientService.enrichRecipient(newMessage.getRecipients());
        else throw new UserException(messageHelper.obtainMessage("messaging.exception.message.recipients.empty"));
    }

    /**
     * Построение уведомления по шаблону и отправка
     *
     * @param templateMessageOutbox Уведомление для построения по шаблону
     */
    private void buildAndSendMessage(TemplateMessageOutbox templateMessageOutbox) {
        MessageTemplate template = messageTemplateService.getTemplate(templateMessageOutbox.getTenantCode(), templateMessageOutbox.getTemplateCode());

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

        Message newMessage = message.clone();
        Optional.ofNullable(attachmentService).ifPresent(as -> newMessage.setAttachments(as.findAll(UUID.fromString(message.getId()))));

        mqProvider.publish(newMessage, channel.getQueueName());
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
}
