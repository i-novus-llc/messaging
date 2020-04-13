package ru.inovus.messaging.api.queue;

import ru.inovus.messaging.api.MessageOutbox;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @author RMakhmutov
 * @since 02.04.2019
 */
public class TopicMqConsumer implements MqConsumer {
    private String topicName;
    private Consumer<MessageOutbox> messageHandler;
    private Serializable subscriber;

    public String systemId;
    public String authToken;

    public TopicMqConsumer(Serializable subscriber, String systemId, String authToken, String topicName, Consumer<MessageOutbox> messageHandler) {
        this.topicName = topicName;
        this.messageHandler = messageHandler;
        this.subscriber = subscriber;
        this.systemId = systemId;
        this.authToken = authToken;
    }

    @Override
    public Consumer<MessageOutbox> messageHandler() {
        return messageHandler;
    }

    @Override
    public String mqName() {
        return topicName;
    }

    @Override
    public Serializable subscriber() {
        return subscriber;
    }
}
