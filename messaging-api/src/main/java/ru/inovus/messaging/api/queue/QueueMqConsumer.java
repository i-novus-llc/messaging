package ru.inovus.messaging.api.queue;

/**
 * @author RMakhmutov
 * @since 02.04.2019
 */

import ru.inovus.messaging.api.MessageOutbox;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 *
 */
public class QueueMqConsumer implements MqConsumer {
    private String queueName;
    private Consumer<MessageOutbox> messageHandler;
    private Serializable consumerUniqueName;

    public QueueMqConsumer(String queueName, Consumer<MessageOutbox> messageHandler, Serializable consumerUniqueName) {
        this.queueName = queueName;
        this.messageHandler = messageHandler;
        this.consumerUniqueName = consumerUniqueName;
    }

    @Override
    public Consumer<MessageOutbox> messageHandler() {
        return messageHandler;
    }

    @Override
    public String mqName() {
        return queueName;
    }

    @Override
    public Serializable subscriber() {
        return consumerUniqueName;
    }
}
