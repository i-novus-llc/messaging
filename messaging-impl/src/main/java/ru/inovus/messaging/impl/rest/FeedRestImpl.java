package ru.inovus.messaging.impl.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.FeedCriteria;
import ru.inovus.messaging.api.model.Feed;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.rest.FeedRest;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.impl.service.FeedService;
import ru.inovus.messaging.impl.service.MessageService;

import java.util.UUID;

@Controller
public class FeedRestImpl implements FeedRest {

    @Value("${novus.messaging.queue.feed-count}")
    private String feedCountQueue;

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
    public FeedCount getFeedCount(String username, String systemId) {
        return feedService.getFeedCount(username, systemId);
    }

    @Override
    public Feed getMessage(String recipient, UUID id) {
        return feedService.getMessage(id, recipient);
    }

    @Override
    public Feed getMessageAndRead(String recipient, UUID messageId) {
        Feed result = feedService.getMessageAndRead(messageId, recipient);
        if (result != null) {
            mqProvider.publish(getFeedCount(recipient, result.getSystemId()), feedCountQueue);
        }
        return result;
    }

    @Override
    public void markReadAll(String recipient, String systemId) {
        feedService.markReadAll(recipient, systemId);
        mqProvider.publish(new FeedCount(systemId, recipient, 0), feedCountQueue);
    }

    @Override
    public void markRead(String recipient, UUID messageId) {
//      todo 3 обращения к бд, как то не очень
        feedService.markRead(recipient, messageId);
        Message message = messageService.getMessage(messageId);
        mqProvider.publish(getFeedCount(recipient, message.getSystemId()), feedCountQueue);
    }
}
