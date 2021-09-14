package ru.inovus.messaging.web.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageOutbox;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.channel.api.queue.AbstractChannel;
import ru.inovus.messaging.channel.api.queue.MqConsumer;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.TopicMqConsumer;
import ru.inovus.messaging.web.channel.controller.MessageController;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class MessagingChannel extends AbstractChannel {

    private static final Logger logger = LoggerFactory.getLogger(MessagingChannel.class);

    @Value("${messaging.channel.web-notice-topic}")
    private String noticeTopicName;

    @Value("${messaging.message-lifetime}")
    private Integer timeout;

    private final MessageController messageController;

    private final MqProvider mqProvider;

    public MessagingChannel(@Value("${messaging.channel.web-queue-name}") String queueName, MqProvider mqProvider, MessageController messageController, MqProvider countProvider) {
        super(queueName, mqProvider);
        this.messageController = messageController;
        this.mqProvider = countProvider;
    }

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
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
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        mqProvider.unsubscribe(event.getSessionId());
    }

    @Override
    public void send(MessageOutbox message) {
        mqProvider.publish(message, noticeTopicName);
    }

    @Override
    public void reportSendStatus() {
        //todo очередь статусов
    }

    private void sendTo(MessageOutbox msg, SimpMessageHeaderAccessor headers) {
        String systemId = getSystemId(headers.getDestination());
        String recipient = headers.getUser().getName();
        if (checkRecipient(msg, recipient, systemId)) {
            if (msg.getMessage() != null) {
                Message message = msg.getMessage();

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

    private String getSystemId(String dest) {
        String result = dest.replace("/user/exchange/", "");
        return result.substring(0, result.indexOf("/"));
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

    private static boolean isNotExpired(LocalDateTime start, LocalDateTime end, Integer timeout) {
        return start == null || end == null || end.minus(timeout, ChronoUnit.SECONDS).isBefore(start);
    }
}
