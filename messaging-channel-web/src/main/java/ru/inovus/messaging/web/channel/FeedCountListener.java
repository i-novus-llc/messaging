package ru.inovus.messaging.web.channel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.QueueMqConsumer;
import ru.inovus.messaging.web.channel.controller.MessageController;

@Component
public class FeedCountListener {

    private MessageController messageController;

    @Value("${novus.messaging.feed.queue}")
    private String feedCountQueue;

    public FeedCountListener(MessageController messageController, MqProvider mqProvider) {
        this.messageController = messageController;
        mqProvider.subscribe(new QueueMqConsumer(feedCountQueue, message -> sendCount((Message) message), feedCountQueue));
    }

    public void sendCount(Message message) {
        messageController.sendFeedCount(message.getSystemId(),
                message.getRecipients().get(0).getName(), Integer.valueOf(message.getText()));
    }
}
