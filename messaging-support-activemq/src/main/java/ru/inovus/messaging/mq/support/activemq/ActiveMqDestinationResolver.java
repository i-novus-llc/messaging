package ru.inovus.messaging.mq.support.activemq;

import org.apache.activemq.command.ActiveMQDestination;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.channel.api.queue.DestinationResolver;
import ru.inovus.messaging.channel.api.queue.DestinationType;

/**
 * @author RMakhmutov
 * @since 03.04.2019
 */
@Component
public class ActiveMqDestinationResolver implements DestinationResolver {
    @Override
    public String resolve(String mqDestinationName, DestinationType destinationType) {
        switch (destinationType) {
            case QUEUE:
                return ActiveMQDestination.QUEUE_QUALIFIED_PREFIX + mqDestinationName;
            case TOPIC:
                return ActiveMQDestination.TOPIC_QUALIFIED_PREFIX + mqDestinationName;
            default:
                return mqDestinationName;
        }
    }
}
