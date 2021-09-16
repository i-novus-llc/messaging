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

    private String statusQueueName;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final MqProvider mqProvider;

    public MessageController(@Value("${novus.messaging.status.queue}") String statusQueueName,
                             MqProvider mqProvider,
                             SimpMessagingTemplate simpMessagingTemplate) {
        this.statusQueueName = statusQueueName;
        this.mqProvider = mqProvider;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/{systemId}/message.count")
    public void sendFeedCount(@DestinationVariable("systemId") String systemId,
                              String principal,
                              Integer feedCount) {
        simpMessagingTemplate.convertAndSend("/user/" + principal + "/exchange/" + systemId + "/message.count", feedCount);
    }

    @MessageMapping("/{systemId}/message.private.{username}")
    public void sendPrivateMessage(@DestinationVariable("systemId") String systemId,
                                   @DestinationVariable("username") String username,
                                   @Payload Message message) {
        simpMessagingTemplate.convertAndSend("/user/" + username + "/exchange/" + systemId + "/message", message);
    }

    @MessageMapping("/{systemId}/message.markreadall")
    public void markReadAll(@DestinationVariable("systemId") String systemId,
                            Principal principal) {
        //todo очередь статусов
//        feedService.markReadAll(principal.getName(), systemId);
    }

    @MessageMapping("/{systemId}/message.markread")
    public void markRead(@DestinationVariable("systemId") String systemId,
                         @Payload UUID messageId,
                         Principal principal) {
//        todo очередь статусов
//        feedService.markRead(principal.getName(), messageIds.toArray(new UUID[0]));
    }
}
