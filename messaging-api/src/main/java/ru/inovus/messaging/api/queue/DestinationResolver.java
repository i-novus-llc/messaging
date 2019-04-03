package ru.inovus.messaging.api.queue;

/**
 * @author RMakhmutov
 * @since 03.04.2019
 */
public interface DestinationResolver {
    String resolve(String mqDestinationName, DestinationType destinationType);
}
