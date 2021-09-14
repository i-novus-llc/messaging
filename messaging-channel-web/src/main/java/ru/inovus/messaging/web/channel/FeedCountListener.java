package ru.inovus.messaging.web.channel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.api.model.MessageOutbox;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.QueueMqConsumer;
import ru.inovus.messaging.web.channel.controller.MessageController;

@Component
public class FeedCountListener {

    private MessageController messageController;

    @Value("messaging.channel.feed-count-queue-name")
    private String feedCountQueueName;

    public FeedCountListener(MessageController messageController, MqProvider mqProvider) {
        this.messageController = messageController;
        mqProvider.subscribe(new QueueMqConsumer(feedCountQueueName, this::sendCount, feedCountQueueName));
    }

    public void sendCount(MessageOutbox messageOutbox) {
        messageController.sendFeedCount(messageOutbox.getMessage().getSystemId(),
                messageOutbox.getMessage().getRecipients().get(0).getRecipient(), Integer.valueOf(messageOutbox.getMessage().getText()));
    }
}
