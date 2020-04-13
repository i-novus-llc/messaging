package ru.inovus.messaging.api.queue;

import ru.inovus.messaging.api.MessageOutbox;

import java.io.Serializable;

public interface MqProvider {

    void subscribe(MqConsumer mqConsumer);

    void publish(MessageOutbox message, String mqDestinationName);

    void unsubscribe(Serializable subscriber);
}
