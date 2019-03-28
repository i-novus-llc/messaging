package ru.inovus.messaging.api;

import java.io.Serializable;
import java.util.function.Consumer;

public interface MqProvider {

    void subscribe(Serializable subscriber, String systemId, String authToken, Consumer<MessageOutbox> messageHandler);

    void publish(MessageOutbox message);

    void unsubscribe(Serializable subscriber);
}
