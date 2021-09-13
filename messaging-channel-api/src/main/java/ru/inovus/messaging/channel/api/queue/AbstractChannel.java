package ru.inovus.messaging.channel.api.queue;

import ru.inovus.messaging.channel.api.queue.models.MessageStatusQO;

/**
 * Абстрактный канал отправки сообщений
 */
public abstract class AbstractChannel implements Channel {
    private MqProvider mqProvider;
    private String statusQueueName;

    protected AbstractChannel(MqProvider mqProvider, String messageQueueName,
                              String statusQueueName) {
        this.mqProvider = mqProvider;
        this.statusQueueName = statusQueueName;
        mqProvider.subscribe(new QueueMqConsumer<>(messageQueueName, this::send, messageQueueName));
    }

    @Override
    public void reportSendStatus(MessageStatusQO status) {
        mqProvider.publish(status, statusQueueName);
    }
}
