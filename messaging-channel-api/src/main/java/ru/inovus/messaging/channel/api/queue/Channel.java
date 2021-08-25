package ru.inovus.messaging.channel.api.queue;

import ru.inovus.messaging.api.model.MessageOutbox;

public interface Channel {

    void send(MessageOutbox message);

    void reportSendStatus();
}
