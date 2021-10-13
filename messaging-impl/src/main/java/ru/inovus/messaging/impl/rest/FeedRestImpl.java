package ru.inovus.messaging.impl.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.criteria.FeedCriteria;
import ru.inovus.messaging.api.model.Feed;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.api.rest.FeedRest;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.impl.service.FeedService;

import java.util.UUID;

@Controller
public class FeedRestImpl implements FeedRest {

    @Value("${novus.messaging.queue.feed-count}")
    private String feedCountQueue;

    private final FeedService feedService;
    private final MqProvider mqProvider;

    public FeedRestImpl(FeedService feedService, MqProvider mqProvider) {
        this.feedService = feedService;
        this.mqProvider = mqProvider;
    }

    @Override
    public Page<Feed> getMessageFeed(String tenantCode, String username, FeedCriteria criteria) {
        return feedService.getMessageFeed(tenantCode, username, criteria);
    }

    @Override
    public FeedCount getFeedCount(String tenantCode, String username) {
        return feedService.getFeedCount(tenantCode, username);
    }

    @Override
    public Feed getMessage(String tenantCode, String username, UUID id) {
        return feedService.getMessage(id, username);
    }

    @Override
    public Feed getMessageAndRead(String tenantCode, String username, UUID messageId) {
        Feed result = feedService.getMessageAndRead(messageId, username);
        if (result != null) {
            mqProvider.publish(getFeedCount(tenantCode, username), feedCountQueue);
        }
        return result;
    }

    @Override
    public void markReadAll(String tenantCode, String username) {
        feedService.markReadAll(tenantCode, username);
        mqProvider.publish(new FeedCount(tenantCode, username, 0), feedCountQueue);
    }

    @Override
    public void markRead(String tenantCode, String username, UUID messageId) {
        feedService.markRead(username, messageId);
        mqProvider.publish(getFeedCount(tenantCode, username), feedCountQueue);
    }
}
