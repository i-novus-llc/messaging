package ru.inovus.messaging.server.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.inovus.messaging.api.*;
import ru.inovus.messaging.impl.MessageService;
import ru.inovus.messaging.server.model.SocketEvent;
import ru.inovus.messaging.server.model.SocketEventType;
import ru.inovus.messaging.api.MqProvider;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class SocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);

    private final ObjectMapper mapper;
    private final MqProvider mqProvider;
    private final MessageService messageService;
    public static final String AUTH_TOKEN_HEADER = "X-Auth-Token";
    public static final String SYSTEM_ID_HEADER = "X-System-Id";

    private Integer timeout;

    @Value("${novus.messaging.timeout}")
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public SocketHandler(ObjectMapper mapper, MqProvider mqProvider, MessageService messageService) {
        this.mapper = mapper;
        this.mqProvider = mqProvider;
        this.messageService = messageService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SocketEvent socketEvent = mapper.readValue(message.getPayload(), SocketEvent.class);
        handleEvent(session, socketEvent);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        mqProvider.unsubscribe(session.getId());
    }

    private void handleEvent(WebSocketSession session, SocketEvent socketEvent) {
        String authToken = socketEvent.getHeaders().get(AUTH_TOKEN_HEADER);
        String systemId = socketEvent.getHeaders().get(SYSTEM_ID_HEADER);
        if (SocketEventType.READ.equals(socketEvent.getType())) {
            if (socketEvent.getMessage() != null)
                messageService.markRead(systemId, socketEvent.getMessage().getId());
            else
                messageService.markReadAll(authToken, systemId);
        }
        UnreadMessagesInfo unreadMessages = messageService.getUnreadMessages(authToken, systemId);
        sendMessage(session, unreadMessages);
        if (SocketEventType.CONNECT.equals(socketEvent.getType())) {
            mqProvider.subscribe(session.getId(), systemId, authToken, messageOutbox ->
                    sendTo(session, messageOutbox, authToken, systemId));
        }
    }

    private boolean isNotExpired(MessageOutbox msg) {
        return msg.getMessage().getSentAt() == null ||
                msg.getMessage().getSentAt().plus(timeout, ChronoUnit.SECONDS)
                        .isBefore(LocalDateTime.now(Clock.systemUTC()));
    }

    private boolean checkRecipient(MessageOutbox msg, String recipient, String systemId) {
        if (msg == null || msg.getRecipients() == null || msg.getRecipients().isEmpty())
            return false;
        for (Recipient r : msg.getRecipients()) {
            if (r.getRecipientType() == RecipientType.ALL)
                return true;
            if (r.getUser().equals(recipient) && r.getSystemId().equals(systemId))
                return true;
        }
        return false;
    }

    private void sendMessage(WebSocketSession session, Object message) {
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTo(WebSocketSession user, MessageOutbox msg, String recipient, String systemId) {
        if (msg.getCommand() != null) {
            messageService.markRead(systemId, msg.getCommand().getMessageIds().toArray(new String[0]));
            sendMessage(user, messageService.getUnreadMessages(recipient, systemId));
        } else if (checkRecipient(msg, recipient, systemId)) {
            if (msg.getMessage() != null) {
                Message message = messageService.createMessage(msg.getMessage(), recipient, systemId);
                UnreadMessagesInfo unreadMessages = messageService.getUnreadMessages(recipient, systemId);
                sendMessage(user, unreadMessages);
                if (isNotExpired(msg)) {
                    sendMessage(user, message);
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Did not send message with id {} due to expiration {}",
                            message.getId(), message.getSentAt());
                }
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug("No recipients for message");
        }
    }

}
