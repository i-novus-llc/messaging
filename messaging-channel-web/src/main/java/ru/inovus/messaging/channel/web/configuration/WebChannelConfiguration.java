package ru.inovus.messaging.channel.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.web.FeedCountListener;
import ru.inovus.messaging.channel.web.WebChannel;
import ru.inovus.messaging.channel.web.controller.MessageController;

@Configuration
@Import({WebSocketConfiguration.class, MessageController.class})
@EnableConfigurationProperties({WebChannelProperties.class})
public class WebChannelConfiguration {

    @Value("${novus.messaging.queue.status}")
    String statusQueueName;
    @Value("${novus.messaging.queue.feed-count}")
    String feedCountQueue;

    @Bean
    WebChannel webChannel(WebChannelProperties webChannelProperties,
                          MqProvider mqProvider,
                          MessageController messageController) {
        WebChannel webChannel = new WebChannel(webChannelProperties.getQueue(), statusQueueName, mqProvider, messageController);
        webChannel.setTimeout(webChannelProperties.getMessageLifetime());
        webChannel.setNoticeTopicName(webChannelProperties.getNoticeTopicName());
        return webChannel;
    }

    @Bean
    FeedCountListener feedCountListener(MqProvider mqProvider,
                                        MessageController messageController) {
        return new FeedCountListener(feedCountQueue, mqProvider, messageController);
    }
}

