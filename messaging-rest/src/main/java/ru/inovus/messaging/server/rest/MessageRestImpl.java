package ru.inovus.messaging.server.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.MqProvider;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.rest.MessageRest;
import ru.inovus.messaging.impl.MessageService;

@Controller
public class MessageRestImpl implements MessageRest {

    private static final Logger log = LoggerFactory.getLogger(MessageRestImpl.class);

    private final MessageService messageService;
    private final Long timeout;
    private final MqProvider mqProvider;
    private final String topic;
    private final String emailTopic;

    public MessageRestImpl(MessageService messageService,
                           @Value("${novus.messaging.timeout}") Long timeout,
                           @Value("${novus.messaging.topic.notice}") String topic,
                           @Value("${novus.messaging.topic.email}") String emailTopic,
                           MqProvider mqProvider) {
        this.messageService = messageService;
        this.timeout = timeout;
        this.mqProvider = mqProvider;
        this.topic = topic;
        this.emailTopic = emailTopic;
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
    public void sendMessage(final MessageOutbox message) {
        if (message.getMessage() != null) {
            Recipient[] init = new Recipient[0];
            Recipient[] recipients = message.getMessage().getRecipients() != null ?
                message.getMessage().getRecipients().toArray(init) : init;
            Message msg = messageService.createMessage(message.getMessage(), recipients);
            message.getMessage().setId(msg.getId());
        }
        mqProvider.publish(message);

    }

    public void markRead(String recipient, String id) {
        messageService.markRead(recipient, id);
    }
}
