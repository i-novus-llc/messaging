package ru.inovus.messaging.server.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.inovus.messaging.api.*;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.RecipientType;
import ru.inovus.messaging.api.queue.MqProvider;
import ru.inovus.messaging.api.queue.TopicMqConsumer;
import ru.inovus.messaging.impl.MessageService;
import ru.inovus.messaging.server.auth.WebSocketAuthenticator;
import ru.inovus.messaging.server.model.SocketEvent;
import ru.inovus.messaging.server.model.SocketEventType;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class SocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);

    public static final String AUTH_TOKEN_HEADER = "X-Auth-Token";
    public static final String SYSTEM_ID_HEADER = "X-System-Id";

    private Integer timeout;

    private WebSocketAuthenticator authenticator;

    private ObjectMapper mapper;

    private MqProvider mqProvider;

    private MessageService messageService;

    @Value("${novus.messaging.timeout}")
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @Value("${novus.messaging.topic.notice}")
    private String noticeTopicName;

    @Autowired
    public void setAuthenticator(WebSocketAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Autowired
    public void setMqProvider(MqProvider mqProvider) {
        this.mqProvider = mqProvider;
    }

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
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
        String systemId = socketEvent.getHeaders().get(SYSTEM_ID_HEADER);
        if (SocketEventType.CONNECT.equals(socketEvent.getType())) {
            String authToken = socketEvent.getHeaders().get(AUTH_TOKEN_HEADER);
            Authentication user = authenticator.authenticate(authToken);
            if (user == null) {
                try {
                    session.close(CloseStatus.NOT_ACCEPTABLE);
                } catch (IOException e) {
                    logger.error("Close error", e);
                }
                return;
            }
            SecurityContextHolder.setContext(new SecurityContextImpl(user));
            UnreadMessagesInfo unreadMessages = messageService.getUnreadMessages(user.getName(), systemId);
            sendMessage(session, unreadMessages);
            mqProvider.subscribe(new TopicMqConsumer(session.getId(), systemId, user.getName(), noticeTopicName, messageOutbox ->
                    sendTo(session, messageOutbox, authToken, systemId)));
        }
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        UnreadMessagesInfo unreadMessages = messageService.getUnreadMessages(userName, systemId);
        sendMessage(session, unreadMessages);
        if (SocketEventType.READ.equals(socketEvent.getType())) {
            if (socketEvent.getMessage() != null)
                messageService.markRead(userName, socketEvent.getMessage().getId());
            else
                messageService.markReadAll(userName, systemId);
        }

    }

    private boolean isNotExpired(Message msg) {
        return isNotExpired(msg.getSentAt(), LocalDateTime.now(Clock.systemUTC()), timeout);
    }

    /**
     * the law: {@code end - start <= timeout => end - timeout <= start}
     */
    public static boolean isNotExpired(LocalDateTime start, LocalDateTime end, Integer timeout) {
        return start == null || end == null || end.minus(timeout, ChronoUnit.SECONDS).isBefore(start);
    }

    private boolean checkRecipient(MessageOutbox msg, String recipient, String systemId) {
        if (msg != null && msg.getMessage() != null && RecipientType.ALL.equals(msg.getMessage().getRecipientType()))
            return true;
        if (msg == null || msg.getMessage() == null || msg.getMessage().getRecipients() == null || msg.getMessage().getRecipients().isEmpty())
            return false;
        for (Recipient r : msg.getMessage().getRecipients()) {
            if (r.getRecipient().equals(recipient) && msg.getMessage().getSystemId().equals(systemId))
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
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            messageService.markRead(userName, msg.getCommand().getMessageIds().toArray(new String[0]));
            sendMessage(user, msg.getCommand());
        } else if (checkRecipient(msg, recipient, systemId)) {
            if (msg.getMessage() != null) {
                Message message = msg.getMessage();
                messageService.setSentTime(systemId,message.getId());
                UnreadMessagesInfo unreadMessages = messageService.getUnreadMessages(recipient, systemId);
                sendMessage(user, unreadMessages);
                if (isNotExpired(message)) {
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
