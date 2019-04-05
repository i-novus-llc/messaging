package ru.inovus.messaging.server.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.InfoType;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.queue.MqProvider;
import ru.inovus.messaging.api.rest.MessageRest;
import ru.inovus.messaging.impl.MessageService;
import ru.inovus.messaging.api.queue.DestinationResolver;
import ru.inovus.messaging.api.queue.DestinationType;

@Controller
public class MessageRestImpl implements MessageRest {

    private static final Logger log = LoggerFactory.getLogger(MessageRestImpl.class);

    private final MessageService messageService;
    private final Long timeout;
    private final MqProvider mqProvider;
    private final String noticeTopicName;
    private final String emailTopicName;
    private DestinationResolver destinationResolver;

    public MessageRestImpl(MessageService messageService,
                           @Value("${novus.messaging.timeout}") Long timeout,
                           @Value("${novus.messaging.topic.notice}") String noticeTopicName,
                           @Value("${novus.messaging.topic.email}") String emailTopicName,
                           MqProvider mqProvider,
                           DestinationResolver destinationResolver) {
        this.messageService = messageService;
        this.timeout = timeout;
        this.mqProvider = mqProvider;
        this.noticeTopicName = noticeTopicName;
        this.emailTopicName = emailTopicName;
        this.destinationResolver = destinationResolver;
    }

    @Override
    public Page<Message> getMessages(MessageCriteria criteria) {
        return messageService.getMessages(criteria);
    }

    @Override
    public Message getMessage(String id) {
        return messageService.getMessage(id);
    }

    @Override
    public void sendMessage(final MessageOutbox messageOutbox) {
        if (messageOutbox.getMessage() != null)
            save(messageOutbox.getMessage());

        send(messageOutbox);
    }

    private void save(Message message) {
        Message savedMessage = messageService.createMessage(message, message.getRecipients() == null ? null : message.getRecipients().toArray(new Recipient[0]));
        message.setId(savedMessage.getId());
    }

    private void send(MessageOutbox messageOutbox) {
        for (InfoType infoType : messageOutbox.getMessage().getInfoTypes())
            mqProvider.publish(messageOutbox, destinationResolver.resolve(getDestinationMqName(infoType), getDestinationType(infoType)));
    }

    private DestinationType getDestinationType(InfoType infoType) {
        switch (infoType) {
            case EMAIL:
                return DestinationType.CONSUMER;
            case NOTICE:
                return DestinationType.SUBSCRIBER;
            default:
                return null;
        }
    }

    private String getDestinationMqName(InfoType infoType) {
        switch (infoType) {
            case EMAIL:
                return emailTopicName;
            case NOTICE:
                return noticeTopicName;
            default:
                return null;
        }
    }

    public void markRead(String recipient, String id) {
        messageService.markRead(recipient, id);
    }
}
