package ru.inovus.messaging.mq.support.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.api.Message;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.MqProvider;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class KafkaMqProvider implements MqProvider {

    private Map<Serializable, MessageListenerContainer> containers = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, MessageOutbox> kafkaTemplate;

    private final KafkaProperties properties;
    private final String topic;

    public KafkaMqProvider(KafkaTemplate<String, MessageOutbox> kafkaTemplate,
                           KafkaProperties properties,
                           @Value("${novus.messaging.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
        this.topic = topic;
    }

    @Override
    public void subscribe(Serializable subscriber, String systemId, String authToken,
                          Consumer<MessageOutbox> messageHandler) {
        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setMessageListener((MessageListener<String, MessageOutbox>)
                data -> messageHandler.accept(data.value()));
        Map<String, Object> consumerConfigs = getConsumerConfigs(authToken, systemId);
        containers.put(subscriber, createContainer(consumerConfigs, containerProperties));
    }

    @Override
    public void publish(MessageOutbox message) {
        kafkaTemplate.send(topic, String.valueOf(System.currentTimeMillis()), message);
    }

    @Override
    public void unsubscribe(Serializable subscriber) {
        MessageListenerContainer container = containers.remove(subscriber);
        if (container != null)
            container.stop();
    }

    private MessageListenerContainer createContainer(Map<String, Object> consumerConfigs,
                                                     ContainerProperties containerProperties) {
        DefaultKafkaConsumerFactory<String, Message> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerConfigs);
        KafkaMessageListenerContainer<String, Message> container =
                new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container.start();
        return container;
    }

    private Map<String, Object> getConsumerConfigs(String authToken, String systemId) {
        Map<String, Object> consumerConfigs = properties.buildConsumerProperties();
        consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, topic + "." + systemId + "." + authToken);
        consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ObjectSerializer.class);
        consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return consumerConfigs;
    }

}
