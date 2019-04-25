package ru.inovus.messaging.server;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.ControlMessage;
import ru.inovus.messaging.api.UnreadMessagesInfo;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.impl.FeedService;

import java.security.Principal;
import java.util.Collection;

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
        UnreadMessagesInfo unreadMessages = feedService.getFeedCount(principal.getName(), systemId);
        simpMessagingTemplate.convertAndSend("/user/" + principal.getName() + "/exchange/" + systemId + "/message.count", unreadMessages);
    }

    @MessageMapping("/{systemId}/message.private.{username}")
    public void sendPrivateMessage(@Payload Message message,
                                   @DestinationVariable("username") String username,
                                   @DestinationVariable("systemId") String systemId) {
        simpMessagingTemplate.convertAndSend("/user/" + username + "/exchange/" + systemId + "/message", message);
    }

    public void sendPrivateCommand(@Payload ControlMessage message,
                                   @DestinationVariable("username") String username,
                                   String systemId) {
        simpMessagingTemplate.convertAndSend("/user/" + username + "/exchange/" + systemId + "/message", message);
    }

    @MessageMapping("/{systemId}/message.markreadall")
    public void receiveMarkReadAll(@DestinationVariable("systemId") String systemId,
                                   Principal principal) {
        feedService.markReadAll(principal.getName(), systemId);
    }

    @MessageMapping("/{systemId}/message.markread")
    public void receiveMarkRead(@Payload Collection<String> messageIds,
                                @DestinationVariable("systemId") String systemId,
                                Principal principal) {
        feedService.markRead(principal.getName(), messageIds.toArray(new String[0]));
    }
}
