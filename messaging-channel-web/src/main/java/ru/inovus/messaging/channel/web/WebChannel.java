package ru.inovus.messaging.channel.web;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.channel.api.AbstractChannel;
import ru.inovus.messaging.channel.api.queue.MqConsumer;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.TopicMqConsumer;
import ru.inovus.messaging.channel.web.controller.MessageController;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Реализация канала отправки уведомлений через Web c использованием WebSocket
 */
@Slf4j
public class WebChannel extends AbstractChannel {

    @Setter
    private String webTopicName;

    @Setter
    private Integer timeout;

    private final MessageController messageController;

    private final MqProvider mqProvider;

    public WebChannel(String messageQueueName,
                      String statusQueueName,
                      MqProvider mqProvider,
                      MessageController messageController) {
        super(mqProvider, messageQueueName, statusQueueName);
        this.messageController = messageController;
        this.mqProvider = mqProvider;
    }

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String dest = headers.getDestination();
        if (dest != null && dest.endsWith("/message") && headers.getUser() != null) {
            MqConsumer consumer = new TopicMqConsumer(headers.getSessionId(), getTenantCode(dest), headers.getUser().getName(),
                    webTopicName, message -> sendTo((Message) message, headers));
            mqProvider.subscribe(consumer);
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        mqProvider.unsubscribe(event.getSessionId());
    }

    @Override
    public void send(Message message) {
        mqProvider.publish(message, webTopicName);
    }

    private void sendTo(Message message, SimpMessageHeaderAccessor headers) {
        String tenantCode = getTenantCode(headers.getDestination());
        String recipient = headers.getUser() != null ? headers.getUser().getName() : null;
        if (checkRecipient(message, recipient, tenantCode)) {
            if (isNotExpired(message))
                messageController.sendPrivateMessage(tenantCode, recipient, message);
            else
                log.info("Did not send message with id {} due to expiration {}",
                        message.getId(), message.getSentAt());
        } else
            log.info("No recipients for message");
    }

    private String getTenantCode(String dest) {
        String result = dest.replaceFirst("/user/(.+/)?exchange/", "");
        return result.substring(0, result.indexOf("/"));
    }

    private boolean checkRecipient(Message message, String recipient, String tenantCode) {
        if (message == null || isEmpty(message.getRecipients()))
            return false;
        for (Recipient r : message.getRecipients()) {
            if (r.getUsername().equals(recipient) && message.getTenantCode().equals(tenantCode))
                return true;
        }
        return false;
    }

    private boolean isNotExpired(Message msg) {
        LocalDateTime start = msg.getSentAt();
        LocalDateTime end = LocalDateTime.now(Clock.systemUTC());
        return start == null || end.minus(timeout, ChronoUnit.SECONDS).isBefore(start);
    }
}
