package ru.inovus.messaging.server.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.UnreadMessagesInfo;
import ru.inovus.messaging.api.criteria.FeedCriteria;
import ru.inovus.messaging.api.model.Feed;
import ru.inovus.messaging.api.rest.FeedRest;
import ru.inovus.messaging.impl.FeedService;
import ru.inovus.messaging.server.MessageController;

@Controller
public class FeedRestImpl implements FeedRest {

    private static final Logger log = LoggerFactory.getLogger(FeedRestImpl.class);

    private final FeedService feedService;

    @Autowired
    private MessageController messageController;

    public FeedRestImpl(FeedService feedService) {
        this.feedService = feedService;
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
    public Feed getMessage(String id, String recipient) {
        return feedService.getMessage(id, recipient);
    }

    @Override
    public Feed getMessageAndRead(String recipient, String id) {
        Feed result = feedService.getMessageAndRead(id, recipient);
        if (result != null) {
            messageController.sendFeedCount(result.getSystemId(), recipient);
        }
        return result;
    }

    @Override
    public void markReadAll(String recipient, String systemId) {
        feedService.markReadAll(recipient, systemId);
        messageController.sendFeedCount(systemId, recipient);
    }

}
