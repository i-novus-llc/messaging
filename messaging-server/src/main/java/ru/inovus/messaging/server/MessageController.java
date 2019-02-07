package ru.inovus.messaging.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.*;
import ru.inovus.messaging.impl.MessageService;

import java.time.Clock;
import java.time.LocalDateTime;

@Controller
public class MessageController implements MessagingRest {

    private final MessageService messageService;
    private final Long timeout;
    private final MqProvider mqProvider;

    public MessageController(MessageService messageService,
                             @Value("${novus.messaging.timeout}") Long timeout,
                             MqProvider mqProvider) {
        this.messageService = messageService;
        this.timeout = timeout;
        this.mqProvider = mqProvider;
    }

    @Override
    public Page<Message> getMessages(MessagingCriteria criteria) {
         return messageService.getMessages(criteria);
    }

    @Override
    public Message getMessage(String id) {
        return messageService.getMessage(id);
    }

    @Override
    public void sendMessage(final MessageOutbox message) {
        if (message.getMessage() != null) {
            if (message.getMessage().getSentAt() == null)
                message.getMessage().setSentAt(LocalDateTime.now(Clock.systemUTC()));
        }
        mqProvider.publish(message);
    }

    @Override
    public void markRead(String id) {
        messageService.markRead(id);
    }
}
