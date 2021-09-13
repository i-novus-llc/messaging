package ru.inovus.messaging.channel.api.queue;

import ru.inovus.messaging.channel.api.queue.models.MessageQO;
import ru.inovus.messaging.channel.api.queue.models.MessageStatusQO;

/**
 * АPI канала отправки сообщений
 */
public interface Channel {

    void send(MessageQO message);

    void reportSendStatus(MessageStatusQO status);
}
