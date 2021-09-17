package ru.inovus.messaging.channel.api;

import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageStatus;

/**
 * АPI канала отправки уведомлений
 */
public interface MessageChannel {

    /**
     * Отправка уведомления получателю
     *
     * @param message Сообщение для отправки
     */
    void send(Message message);

    /**
     * Отправка информации о статусе уведомления
     *
     * @param status Сообщение с информацией о статусе уведомления
     */
    void sendStatus(MessageStatus status);
}
