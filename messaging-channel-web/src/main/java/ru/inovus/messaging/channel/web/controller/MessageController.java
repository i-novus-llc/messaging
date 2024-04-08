package ru.inovus.messaging.channel.web.controller;

import net.n2oapp.framework.boot.stomp.WebSocketController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.api.rest.FeedRest;
import ru.inovus.messaging.channel.api.queue.MqProvider;

import java.security.Principal;
import java.util.Map;

/**
 * Контроллер для отправки/получения уведомлений через WebSocket
 */
@Controller
public class MessageController {

    private String statusQueueName;

    private final WebSocketController webSocketController;

    private final MqProvider mqProvider;

    private final FeedRest feedRest;

    public MessageController(@Value("${novus.messaging.queue.status}") String statusQueueName,
                             MqProvider mqProvider,
                             WebSocketController webSocketController,
                             FeedRest feedRest) {
        this.statusQueueName = statusQueueName;
        this.mqProvider = mqProvider;
        this.webSocketController = webSocketController;
        this.feedRest = feedRest;
    }

    /**
     * Отправка пользователю количества непрочитанных уведомлений
     *
     * @param feedCount Информация о непрочитанных уведомлениях пользователя
     */
    public void sendFeedCount(FeedCount feedCount) {
        String destination = "/exchange/" + feedCount.getTenantCode() + "/message.count";
        webSocketController.convertAndSendToUser(feedCount.getUsername(), destination, getFeedCountPayload(feedCount));
    }

    /**
     * Запросить количество непрочитанных сообщений
     *
     * @param tenantCode Идентификатор системы, в которой находится пользователь
     * @param principal  Информация о пользователе
     */
    @MessageMapping("/{tenantCode}/message.getcount")
    public void getFeedCount(@DestinationVariable("tenantCode") String tenantCode, Principal principal) {
        FeedCount feedCount = feedRest.getFeedCount(tenantCode, principal.getName());
        sendFeedCount(feedCount);
    }

    /**
     * Отправка уведомления пользователю
     *
     * @param tenantCode Идентификатор системы, в которой находится пользователь
     * @param username   Имя пользователя
     * @param message    Уведомление
     */
    public void sendPrivateMessage(String tenantCode,
                                   String username,
                                   @Payload Message message) {
        MessageStatus status = new MessageStatus();
        status.setTenantCode(tenantCode);
        status.setMessageId(message.getId());
        status.setUsername(username);
        try {
            String destination = "/exchange/" + tenantCode + "/message";
            message.setSeverityId(message.getSeverity().getId().toLowerCase());
            webSocketController.convertAndSendToUser(username, destination, message);
            status.setStatus(MessageStatusType.SENT);
            mqProvider.publish(status, statusQueueName);
        } catch (MessagingException e) {
            status.setStatus(MessageStatusType.FAILED);
            status.setErrorMessage(e.getMessage());
            mqProvider.publish(status, statusQueueName);
            throw e;
        }
    }

    /**
     * Отметить прочитанными все уведомления пользователя
     *
     * @param tenantCode Идентификатор системы, в которой находится пользователь
     * @param principal  Информация о пользователе
     */
    @MessageMapping("/{tenantCode}/message.markreadall")
    public void markReadAll(@DestinationVariable("tenantCode") String tenantCode,
                            Principal principal) {
        MessageStatus status = new MessageStatus();
        status.setTenantCode(tenantCode);
        status.setUsername(principal.getName());
        status.setStatus(MessageStatusType.READ);
        mqProvider.publish(status, statusQueueName);
    }

    /**
     * Отметить уведомление, прочитанным пользователем
     *
     * @param tenantCode Идентификатор системы, в которой находится пользователь
     * @param messageId  Идентификатор уведомления
     * @param principal  Информация о пользователе
     */
    @MessageMapping("/{tenantCode}/message.markread")
    public void markRead(@DestinationVariable("tenantCode") String tenantCode,
                         @Payload String messageId,
                         Principal principal) {
        MessageStatus status = new MessageStatus();
        status.setTenantCode(tenantCode);
        status.setMessageId(messageId);
        status.setUsername(principal.getName());
        status.setStatus(MessageStatusType.READ);
        mqProvider.publish(status, statusQueueName);
    }

    private Object getFeedCountPayload(FeedCount feedCount) {
        return webSocketController instanceof DefaultSpringWebSocketController
                ? feedCount.getCount()
                : Map.of("message_count", feedCount.getCount());
    }
}