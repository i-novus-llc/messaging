package ru.inovus.messaging.impl.queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.QueueMqConsumer;
import ru.inovus.messaging.impl.service.RecipientService;

import java.util.UUID;

/**
 * Слушатель статусов уведомлений.
 * Необходим для получения статусов отправленных уведомлений
 * из каналов отправки и дальнейшей их обработки.
 */
@Component
public class MessageStatusListener {

    private RecipientService recipientService;

    public MessageStatusListener(@Value("${novus.messaging.status.queue}") String statusQueue,
                                 MqProvider mqProvider,
                                 RecipientService recipientService) {
        this.recipientService = recipientService;
        mqProvider.subscribe(new QueueMqConsumer(statusQueue, this::processStatus, statusQueue));
    }

    /**
     * Обработка полученного статуса
     *
     * @param message Сообщение с информацией о статусе уведомления
     */
    private void processStatus(Message message) {
        recipientService.updateStatus(UUID.fromString(message.getId()), message.getStatus(), message.getSendErrorMessage());
    }
}
