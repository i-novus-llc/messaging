package ru.inovus.messaging.channel.api.queue;

import ru.inovus.messaging.api.model.MessageOutbox;
import ru.inovus.messaging.api.model.enums.SendStatus;

/**
 * АPI канала отправки сообщений
 */
public interface Channel {

    void send(MessageOutbox message);

    void reportSendStatus(SendStatus status);
}
