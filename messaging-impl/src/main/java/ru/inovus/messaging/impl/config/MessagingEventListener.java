package ru.inovus.messaging.impl.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.channel.api.queue.MqConsumer;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.TopicMqConsumer;
import ru.inovus.messaging.impl.MessageController;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Обработчик событий подписки и отключения от Messaging
 */

public class MessagingEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MessagingEventListener.class);

    @Value("${novus.messaging.topic.notice}")
    private String noticeTopicName;

    @Value("${novus.messaging.timeout}")
    private Integer timeout;

    @Autowired
    private MessageController messageController;

    @Autowired
    private MqProvider mqProvider;


    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String dest = headers.getDestination();
        if (dest.endsWith("/message.count")) {
//            messageController.sendFeedCount(getSystemId(dest), headers.getUser());
        } else if (dest.endsWith("/message")) {
            MqConsumer consumer = new TopicMqConsumer(headers.getSessionId(), getSystemId(dest), headers.getUser().getName(),
                    noticeTopicName, message -> sendTo(message, headers));
            mqProvider.subscribe(consumer);
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        mqProvider.unsubscribe(event.getSessionId());
    }

    private String getSystemId(String dest) {
        String result = dest.replace("/user/exchange/", "");
        return result.substring(0, result.indexOf("/"));
    }

    private void sendTo(Message message, SimpMessageHeaderAccessor headers) {
        String systemId = getSystemId(headers.getDestination());
        String recipient = headers.getUser().getName();
        if (checkRecipient(message, recipient, systemId)) {
            if (message != null) {
                messageController.sendFeedCount(systemId, headers.getUser());

                if (isNotExpired(message)) {
                    messageController.sendPrivateMessage(message, recipient, systemId);
                } else {
                    logger.debug("Did not send message with id {} due to expiration {}",
                            message.getId(), message.getSentAt());
                }
            }
        } else {
            logger.debug("No recipients for message");
        }
    }

    private boolean checkRecipient(Message message, String recipient, String systemId) {
        if (message != null && RecipientType.ALL.equals(message.getRecipientType()))
            return true;
        if (message == null || message.getRecipients() == null || message.getRecipients().isEmpty())
            return false;
        for (Recipient r : message.getRecipients()) {
            if (r.getName().equals(recipient) && message.getSystemId().equals(systemId))
                return true;
        }
        return false;
    }

    private boolean isNotExpired(Message msg) {
        return isNotExpired(msg.getSentAt(), LocalDateTime.now(Clock.systemUTC()), timeout);
    }

    private static boolean isNotExpired(LocalDateTime start, LocalDateTime end, Integer timeout) {
        return start == null || end == null || end.minus(timeout, ChronoUnit.SECONDS).isBefore(start);
    }

}
