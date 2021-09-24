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

    public FeedCountListener(@Value("${novus.messaging.queue.feed-count}") String feedCountQueue,
                             MqProvider mqProvider,
                             MessageController messageController) {
        this.messageController = messageController;
        mqProvider.subscribe(new QueueMqConsumer(feedCountQueue, message -> sendCount((Message) message), feedCountQueue));
    }

    public void sendCount(Message message) {
        messageController.sendFeedCount(message.getSystemId(),
                message.getRecipients().get(0).getUsername(), Integer.valueOf(message.getText()));
    }
}
