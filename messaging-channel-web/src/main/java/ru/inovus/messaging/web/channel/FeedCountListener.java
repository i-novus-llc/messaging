package ru.inovus.messaging.web.channel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.QueueMqConsumer;
import ru.inovus.messaging.web.channel.controller.MessageController;

/**
 * Слушатель очереди счетчика непрочитанных уведомлений.
 * Необходим для получения статусов уведомлений
 * из каналов отправки и дальнейшей их обработки.
 */
@Component
public class FeedCountListener {

    private MessageController messageController;

    public FeedCountListener(@Value("${novus.messaging.queue.feed-count}") String feedCountQueue,
                             MqProvider mqProvider,
                             MessageController messageController) {
        this.messageController = messageController;
        mqProvider.subscribe(new QueueMqConsumer(feedCountQueue, feedCount -> sendCount((FeedCount) feedCount), feedCountQueue));
    }

    /**
     * Отправка количества непрочитанных уведомлений пользователя
     *
     * @param feedCount Информация о количестве непрочитанных уведомлений
     */
    public void sendCount(FeedCount feedCount) {
        messageController.sendFeedCount(feedCount.getSystemId(), feedCount.getUsername(), feedCount.getCount());
    }
}
