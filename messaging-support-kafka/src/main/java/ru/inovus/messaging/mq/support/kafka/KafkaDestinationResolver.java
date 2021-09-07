package ru.inovus.messaging.mq.support.kafka;

import org.springframework.stereotype.Component;
import ru.inovus.messaging.channel.api.queue.DestinationResolver;
import ru.inovus.messaging.channel.api.queue.DestinationType;

/**
 * @author RMakhmutov
 * @since 03.04.2019
 */
@Component
public class KafkaDestinationResolver implements DestinationResolver {
    @Override
    public String resolve(String mqDestinationName, DestinationType destinationType) {
        return mqDestinationName;
    }
}
