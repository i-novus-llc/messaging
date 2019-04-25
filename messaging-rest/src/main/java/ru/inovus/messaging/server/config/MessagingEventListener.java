package ru.inovus.messaging.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.RecipientType;
import ru.inovus.messaging.api.queue.MqConsumer;
import ru.inovus.messaging.api.queue.MqProvider;
import ru.inovus.messaging.api.queue.TopicMqConsumer;
import ru.inovus.messaging.server.MessageController;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Обработчик событий подписки и отключения от Messaging
 */

public class MessagingEventListener {

    @Value("${novus.messaging.topic.notice}")
    private String noticeTopicName;

    @Value("${novus.messaging.timeout}")
    private Integer timeout;

    @Autowired
    private MessageController messageController;

    @Autowired
    private MqProvider mqProvider;

    private static final Logger logger = LoggerFactory.getLogger(MessagingEventListener.class);

    @EventListener
    private void handleSessionSubscribe(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String dest = headers.getDestination();
        if (dest.endsWith("/message.count")) {
//            messageController.sendFeedCount(getSystemId(dest), headers.getUser());
        } else if (dest.endsWith("/message")) {
            MqConsumer consumer = new TopicMqConsumer(headers.getSessionId(), getSystemId(dest), headers.getUser().getName(),
                    noticeTopicName, messageOutbox -> sendTo(messageOutbox, headers));
            mqProvider.subscribe(consumer);
        }
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {
        mqProvider.unsubscribe(event.getSessionId());
    }

    private String getSystemId(String dest) {
        String result = dest.replace("/user/exchange/", "");
        return result.substring(0, result.indexOf("/"));
    }

    private void sendTo(MessageOutbox msg, SimpMessageHeaderAccessor headers) {
        String systemId = getSystemId(headers.getDestination());
        String recipient = headers.getUser().getName();
        if (msg.getCommand() != null) {
            String userName = headers.getUser().getName();
            messageController.receiveMarkRead(msg.getCommand().getMessageIds(), systemId, headers.getUser());
            messageController.sendPrivateCommand(msg.getCommand(), userName, systemId);
        } else if (checkRecipient(msg, recipient, systemId)) {
            if (msg.getMessage() != null) {
                messageController.sendFeedCount(systemId, headers.getUser());
                Message message = msg.getMessage();

                if (isNotExpired(message)) {
                    messageController.sendPrivateMessage(message, recipient, systemId);
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Did not send message with id {} due to expiration {}",
                            message.getId(), message.getSentAt());
                }
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug("No recipients for message");
        }
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

    private boolean isNotExpired(Message msg) {
        return isNotExpired(msg.getSentAt(), LocalDateTime.now(Clock.systemUTC()), timeout);
    }

    public static boolean isNotExpired(LocalDateTime start, LocalDateTime end, Integer timeout) {
        return start == null || end == null || end.minus(timeout, ChronoUnit.SECONDS).isBefore(start);
    }

}
