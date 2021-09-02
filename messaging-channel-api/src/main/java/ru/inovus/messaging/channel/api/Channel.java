package ru.inovus.messaging.channel.api;

import ru.inovus.messaging.api.queue.DestinationType;
import ru.inovus.messaging.api.queue.MqConsumer;

/**
 * API каналов для отправки уведомлений
 */
public interface Channel {

    MqConsumer getMqConsumer();

    String getDestinationMqName();

    DestinationType getDestinationType();

    String getType();

    String getName();
}
