package ru.inovus.messaging.impl.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.FeedCriteria;
import ru.inovus.messaging.api.model.*;
import ru.inovus.messaging.api.rest.FeedRest;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.impl.service.FeedService;
import ru.inovus.messaging.impl.service.MessageService;

import java.util.List;
import java.util.UUID;

@Controller
public class FeedRestImpl implements FeedRest {

    @Value("messaging.channel.feed-count-queue-name")
    private String feedCountQueueName;

    private final FeedService feedService;
    private final MessageService messageService;
    private final MqProvider mqProvider;

    public FeedRestImpl(FeedService feedService, MessageService messageService, MqProvider mqProvider) {
        this.feedService = feedService;
        this.messageService = messageService;
        this.mqProvider = mqProvider;
    }

    @Override
    public Page<Feed> getMessageFeed(String recipient, FeedCriteria criteria) {
        return feedService.getMessageFeed(recipient, criteria);
    }

    @Override
    public UnreadMessagesInfo getFeedCount(String recipient, String systemId) {
        return feedService.getFeedCount(recipient, systemId);
    }

    @Override
    public Feed getMessage(String recipient, UUID id) {
        return feedService.getMessage(id, recipient);
    }

    @Override
    public Feed getMessageAndRead(String recipient, UUID messageId) {
        Feed result = feedService.getMessageAndRead(messageId, recipient);
        if (result != null) {
            MessageOutbox feedCount = new MessageOutbox();
            feedCount.setMessage(new Message());
            feedCount.getMessage().setText(getFeedCount(recipient, result.getSystemId()).getCount().toString());
            feedCount.getMessage().setSystemId(result.getSystemId());
            feedCount.getMessage().setRecipients(List.of(new Recipient(recipient)));
            mqProvider.publish(feedCount, feedCountQueueName);
        }
        return result;
    }

    @Override
    public void markReadAll(String recipient, String systemId) {
        feedService.markReadAll(recipient, systemId);
        MessageOutbox feedCount = new MessageOutbox();
        feedCount.setMessage(new Message());
        feedCount.getMessage().setText("0");
        feedCount.getMessage().setSystemId(systemId);
        feedCount.getMessage().setRecipients(List.of(new Recipient(recipient)));
        mqProvider.publish(feedCount, feedCountQueueName);
    }

    @Override
    public void markRead(String recipient, UUID messageId) {
//      todo 3 обращения к бд, как то не очень
        feedService.markRead(recipient, messageId);
        Message message = messageService.getMessage(messageId);
        MessageOutbox feedCount = new MessageOutbox();
        feedCount.setMessage(new Message());
        feedCount.getMessage().setText(getFeedCount(recipient, message.getSystemId()).getCount().toString());
        feedCount.getMessage().setSystemId(message.getSystemId());
        feedCount.getMessage().setRecipients(List.of(new Recipient(recipient)));
        mqProvider.publish(feedCount, feedCountQueueName);
    }
}
