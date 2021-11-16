package ru.inovus.messaging.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.UnreadMessagesInfo;
import ru.inovus.messaging.impl.service.FeedService;

import java.security.Principal;
import java.util.Collection;
import java.util.UUID;

/**
 * Контроллер для отправки-приема сообщений ч.з. Spring MessageBroker
 */

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private FeedService feedService;

    @MessageMapping("/{systemId}/message.count")
    public void sendFeedCount(@DestinationVariable("systemId") String systemId,
                              Principal principal) {
        sendFeedCount(systemId, principal.getName());
    }

    public void sendFeedCount(String systemId, String recipient) {
        UnreadMessagesInfo unreadMessages = feedService.getFeedCount(recipient, systemId);
        simpMessagingTemplate.convertAndSend("/user/" + recipient + "/exchange/" + systemId + "/message.count", unreadMessages);
    }

    @MessageMapping("/{systemId}/message.private.{username}")
    public void sendPrivateMessage(@Payload Message message,
                                   @DestinationVariable("username") String username,
                                   @DestinationVariable("systemId") String systemId) {
        simpMessagingTemplate.convertAndSend("/user/" + username + "/exchange/" + systemId + "/message", message);
        sendFeedCount(systemId, username);
    }

    @MessageMapping("/{systemId}/message.markreadall")
    public void receiveMarkReadAll(@DestinationVariable("systemId") String systemId,
                                   Principal principal) {
        feedService.markReadAll(principal.getName(), systemId);
    }

    @MessageMapping("/{systemId}/message.markread")
    public void receiveMarkRead(@Payload Collection<UUID> messageIds,
                                @DestinationVariable("systemId") String systemId,
                                Principal principal) {
        feedService.markRead(principal.getName(), messageIds.toArray(new UUID[0]));
    }
}
