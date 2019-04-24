package ru.inovus.messaging.server;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * Контроллер для отправки-приема сообщений ч.з. Spring MessageBroker
 */

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/chat.message")
    public ChatMessage filterMessage(@Payload ChatMessage message,
                                     Principal principal) {
        message.setUsername(principal.getName());
        return message;
    }

    @MessageMapping("/chat.private.{username}")
    public void filterPrivateMessage(@Payload ChatMessage message,
                                     @DestinationVariable("username") String username,
                                     Principal principal) {
        message.setUsername(principal.getName());
        simpMessagingTemplate.convertAndSend("/user/" + username + "/exchange/amq.direct/chat.message", message);
    }

}
