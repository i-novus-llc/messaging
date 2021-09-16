package ru.inovus.messaging.channel.api.queue;

import ru.inovus.messaging.api.model.Message;

/**
 * АPI канала отправки уведомлений
 */
public interface Channel {

    /**
     * Отправка уведомления получателю
     *
     * @param message Сообщение для отправки
     */
    void send(Message message);

    /**
     * Отправка информации о статусе уведомления
     *
     * @param message Сообщение с информацией о статусе уведомления
     */
    void reportSendStatus(Message message);
}
