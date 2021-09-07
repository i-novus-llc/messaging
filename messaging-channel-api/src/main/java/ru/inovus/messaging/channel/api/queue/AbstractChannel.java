package ru.inovus.messaging.channel.api.queue;

/**
 * Абстрактный канал отправки сообщений
 */
public abstract class AbstractChannel implements Channel {
    protected AbstractChannel(String queueName, MqProvider mqProvider) {
        mqProvider.subscribe(new QueueMqConsumer(queueName, this::send, queueName));
    }
}
