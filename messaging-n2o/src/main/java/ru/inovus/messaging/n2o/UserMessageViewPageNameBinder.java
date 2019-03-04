package ru.inovus.messaging.n2o;

import net.n2oapp.framework.api.data.QueryProcessor;

public class UserMessageViewPageNameBinder extends MessagingPageNameBinder {
    public UserMessageViewPageNameBinder(QueryProcessor queryProcessor) {
        super(queryProcessor, "feed", "messaging_feed");
    }
}
