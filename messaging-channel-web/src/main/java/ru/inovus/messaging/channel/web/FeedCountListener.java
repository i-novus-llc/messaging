package ru.inovus.messaging.channel.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.QueueMqConsumer;
import ru.inovus.messaging.channel.web.controller.MessageController;

/**
 * Слушатель очереди счетчика непрочитанных уведомлений.
 * Необходим для получения статусов уведомлений
 * из каналов отправки и дальнейшей их обработки.
 */
public class FeedCountListener {
    public FeedCountListener(String feedCountQueue,
                             MqProvider mqProvider,
                             MessageController messageController) {
        mqProvider.subscribe(new QueueMqConsumer(feedCountQueue,
                feedCount -> messageController.sendFeedCount((FeedCount) feedCount), feedCountQueue));
    }
}
