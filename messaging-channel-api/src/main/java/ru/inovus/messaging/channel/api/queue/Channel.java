package ru.inovus.messaging.channel.api.queue;

import ru.inovus.messaging.api.model.Message;

/**
 * АPI канала отправки сообщений
 */
public interface Channel {

    void send(Message message);

    void reportSendStatus(Message status);
}
