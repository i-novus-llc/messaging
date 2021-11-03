package ru.inovus.messaging.mq.support.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.channel.api.queue.MqConsumer;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.TopicMqConsumer;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация провайдера очереди объектов на Kafka
 */
public class KafkaMqProvider implements MqProvider {

    private Map<Serializable, MessageListenerContainer> containers = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final KafkaProperties properties;

    public KafkaMqProvider(KafkaTemplate<String, Object> kafkaTemplate,
                           KafkaProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @Override
    public void subscribe(MqConsumer mqConsumer) {
        ContainerProperties containerProperties = new ContainerProperties(mqConsumer.mqName());
        containerProperties.setMessageListener((MessageListener<String, Message>)
                data -> mqConsumer.messageHandler().accept(data.value()));

        containers.put(mqConsumer.subscriber(), createContainer(getConsumerConfigs(mqConsumer), containerProperties));
    }

    @Override
    public void publish(Object object, String queueName) {
        kafkaTemplate.send(queueName, String.valueOf(System.currentTimeMillis()), object);
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

    private Map<String, Object> getConsumerConfigs(MqConsumer mqConsumer) {
        Map<String, Object> consumerConfigs = properties.buildConsumerProperties();
        consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ObjectSerializer.class);
        consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        String groupId = mqConsumer.mqName();
        if (mqConsumer instanceof TopicMqConsumer)
            groupId += "." + ((TopicMqConsumer) mqConsumer).tenantCode + "." + ((TopicMqConsumer) mqConsumer).authToken;
        consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        return consumerConfigs;
    }

}
