package ru.inovus.messaging.channel.api.queue;

import ru.inovus.messaging.api.model.Message;

/**
 * Абстрактный канал отправки уведомлений
 */
public abstract class AbstractChannel implements Channel {
    private MqProvider mqProvider;
    private String statusQueueName;

    protected AbstractChannel(MqProvider mqProvider, String messageQueueName, String statusQueueName) {
        this.mqProvider = mqProvider;
        this.statusQueueName = statusQueueName;
        mqProvider.subscribe(new QueueMqConsumer(messageQueueName, this::send, messageQueueName));
    }

    @Override
    public void reportSendStatus(Message message) {
        mqProvider.publish(message, statusQueueName);
    }
}
