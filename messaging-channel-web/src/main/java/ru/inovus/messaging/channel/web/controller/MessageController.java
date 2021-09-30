package ru.inovus.messaging.channel.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.channel.api.queue.MqProvider;

import java.security.Principal;

/**
 * Контроллер для отправки/получения сообщений через WebSocket
 */
@Controller
public class MessageController {

    private String statusQueueName;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final MqProvider mqProvider;

    public MessageController(@Value("${novus.messaging.queue.status}") String statusQueueName,
                             MqProvider mqProvider,
                             SimpMessagingTemplate simpMessagingTemplate) {
        this.statusQueueName = statusQueueName;
        this.mqProvider = mqProvider;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Отправка пользователю количества непрочитанных сообщений
     *
     * @param systemId  Идентификатор системы, в которой находится пользователь
     * @param username  Имя пользователя
     * @param feedCount Количество непрочитанных сообщений пользователя
     */
    public void sendFeedCount(String systemId,
                              String username,
                              Integer feedCount) {
        simpMessagingTemplate.convertAndSend("/user/" + username + "/exchange/" + systemId + "/message.count", feedCount);
    }

    /**
     * Отправка уведомления пользователю
     *
     * @param systemId Идентификатор системы, в которой находится пользователь
     * @param username Имя пользователя
     * @param message  Сообщение
     */
    public void sendPrivateMessage(String systemId,
                                   String username,
                                   @Payload Message message) {
        MessageStatus status = new MessageStatus();
        status.setSystemId(systemId);
        status.setMessageId(message.getId());
        status.setUsername(username);
        try {
            simpMessagingTemplate.convertAndSend("/user/" + username + "/exchange/" + systemId + "/message", message);
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
     * @param systemId  Идентификатор системы, в которой находится пользователь
     * @param principal Информация о пользователе
     */
    @MessageMapping("/{systemId}/message.markreadall")
    public void markReadAll(@DestinationVariable("systemId") String systemId,
                            Principal principal) {
        MessageStatus status = new MessageStatus();
        status.setSystemId(systemId);
        status.setUsername(principal.getName());
        status.setStatus(MessageStatusType.READ);
        mqProvider.publish(status, statusQueueName);
    }

    /**
     * Отметить уведомление, прочитанным пользователем
     *
     * @param systemId  Идентификатор системы, в которой находится пользователь
     * @param messageId Идентификатор сообщения
     * @param principal Информация о пользователе
     */
    @MessageMapping("/{systemId}/message.markread")
    public void markRead(@DestinationVariable("systemId") String systemId,
                         @Payload String messageId,
                         Principal principal) {
        MessageStatus status = new MessageStatus();
        status.setSystemId(systemId);
        status.setMessageId(messageId);
        status.setUsername(principal.getName());
        status.setStatus(MessageStatusType.READ);
        mqProvider.publish(status, statusQueueName);
    }
}
