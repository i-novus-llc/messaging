package ru.inovus.messaging.channel.api.queue;

public abstract class AbstractChannel implements Channel {
    protected AbstractChannel(String queueName, MqProvider mqProvider) {
        mqProvider.subscribe(new QueueMqConsumer(queueName, this::send, queueName));
    }
}
