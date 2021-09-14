package ru.inovus.messaging.web.channel.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.channel.api.queue.MqProvider;

import java.security.Principal;
import java.util.UUID;

/**
 * Контроллер для отправки-приема сообщений ч.з. Spring MessageBroker
 */

@Controller
public class MessageController {

    @Value("messaging.channel.status-queue-name")
    private String statusQueueName;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final MqProvider mqProvider;

    public MessageController(SimpMessagingTemplate simpMessagingTemplate, MqProvider mqProvider) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.mqProvider = mqProvider;
    }

    @MessageMapping("/{systemId}/message.count")
    public void sendFeedCount(@DestinationVariable("systemId") String systemId,
                              String principal, Integer feedCount) {
        simpMessagingTemplate.convertAndSend("/user/" + principal + "/exchange/" + systemId + "/message.count", feedCount);
    }

    @MessageMapping("/{systemId}/message.private.{username}")
    public void sendPrivateMessage(@Payload Message message,
                                   @DestinationVariable("username") String username,
                                   @DestinationVariable("systemId") String systemId) {
        simpMessagingTemplate.convertAndSend("/user/" + username + "/exchange/" + systemId + "/message", message);
    }

    @MessageMapping("/{systemId}/message.markreadall")
    public void markReadAll(@DestinationVariable("systemId") String systemId,
                            Principal principal) {
        //todo очередь статусов
//        feedService.markReadAll(principal.getName(), systemId);
    }

    @MessageMapping("/{systemId}/message.markread")
    public void markRead(@Payload UUID messageId,
                         @DestinationVariable("systemId") String systemId,
                         Principal principal) {
//        todo очередь статусов
//        feedService.markRead(principal.getName(), messageIds.toArray(new UUID[0]));
    }
}
