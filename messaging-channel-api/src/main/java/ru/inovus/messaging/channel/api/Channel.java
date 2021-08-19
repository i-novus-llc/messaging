package ru.inovus.messaging.channel.api;

import ru.inovus.messaging.api.model.MessageOutbox;

/**
 * API каналов для отправки уведомлений
 */
public interface Channel {

    void sendMessage(MessageOutbox message);

    String getType();

    String getName();
}
