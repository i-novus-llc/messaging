package ru.inovus.messaging.channel.web.controller;

import lombok.RequiredArgsConstructor;
import net.n2oapp.framework.boot.stomp.WebSocketController;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;

/**
 * Контроллер для отправки уведомлений через WebSocket без n2o
 */
@RequiredArgsConstructor
public class DefaultSpringWebSocketController implements WebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void convertAndSend(String destination, Object message) {
        simpMessagingTemplate.convertAndSend(destination, message);
    }

    @Override
    public void convertAndSendToUser(String user, String destination, Object message) {
        simpMessagingTemplate.convertAndSend(getDestination(destination, user), message);
    }

    private String getDestination(String destination, String username) {
        return StringUtils.hasText(username)
                ? "/user/" + username + destination
                : destination;
    }
}
