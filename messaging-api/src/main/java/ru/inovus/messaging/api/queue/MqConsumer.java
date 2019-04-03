package ru.inovus.messaging.api.queue;

import ru.inovus.messaging.api.MessageOutbox;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @author RMakhmutov
 * @since 02.04.2019
 */
public interface MqConsumer<E> {
    Consumer<MessageOutbox> messageHandler();
    String mqName();
    Serializable subscriber();
}
