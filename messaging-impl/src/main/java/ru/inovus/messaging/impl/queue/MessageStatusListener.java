package ru.inovus.messaging.impl.queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.QueueMqConsumer;
import ru.inovus.messaging.impl.service.RecipientService;

/**
 * Слушатель очереди статусов уведомлений.
 * Необходим для получения статусов уведомлений
 * из каналов отправки и дальнейшей их обработки.
 */
@Component
public class MessageStatusListener {

    private RecipientService recipientService;

    public MessageStatusListener(@Value("${novus.messaging.queue.status}") String statusQueue,
                                 MqProvider mqProvider,
                                 RecipientService recipientService) {
        this.recipientService = recipientService;
        mqProvider.subscribe(new QueueMqConsumer(statusQueue, status -> processStatus((MessageStatus) status), statusQueue));
    }

    /**
     * Обработка полученного статуса
     *
     * @param status Сообщение с информацией о статусе уведомления
     */
    private void processStatus(MessageStatus status) {
        recipientService.updateStatus(status);
    }
}
