package ru.inovus.messaging.channel.api;

import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.QueueMqConsumer;
import ru.inovus.messaging.channel.api.queue.model.QueueMessageStatus;

/**
 * Абстрактный канал отправки уведомлений
 */
public abstract class AbstractChannel implements MessageChannel {
    private MqProvider mqProvider;
    private String statusQueueName;

    protected AbstractChannel(MqProvider mqProvider, String messageQueueName, String statusQueueName) {
        this.mqProvider = mqProvider;
        this.statusQueueName = statusQueueName;
        mqProvider.subscribe(new QueueMqConsumer(messageQueueName, message -> send((Message) message), messageQueueName));
    }

    @Override
    public void sendStatus(QueueMessageStatus status) {
        mqProvider.publish(status, statusQueueName);
    }
}
