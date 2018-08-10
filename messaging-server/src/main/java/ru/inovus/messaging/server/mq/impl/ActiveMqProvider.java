package ru.inovus.messaging.server.mq.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.server.mq.MqProvider;

import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
@Profile("activemq")
public class ActiveMqProvider implements MqProvider {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMqProvider.class);

    private final ObjectMapper objectMapper;
    private final ActiveMQConnectionFactory activeMQConnectionFactory;
    private final JmsTemplate jmsTemplate;
    private final String topic;
    private Map<Serializable, DefaultMessageListenerContainer> containers = new ConcurrentHashMap<>();

    public ActiveMqProvider(ObjectMapper objectMapper,
                            @Value("${activemq.broker-url}") String brokerUrl,
                            @Value("${novus.messaging.topic}") String topic) {
        this.objectMapper = objectMapper;
        activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        this.jmsTemplate = new JmsTemplate(new CachingConnectionFactory(activeMQConnectionFactory));;
        this.topic = topic;
    }

    @Override
    public void subscribe(Serializable subscriber, String systemId, String authToken, Consumer<MessageOutbox> messageHandler) {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setMessageListener((MessageListener) message -> {
            try {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    MessageOutbox messageOutbox = objectMapper.readValue(text, MessageOutbox.class);
                    messageHandler.accept(messageOutbox);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        container.setConnectionFactory(activeMQConnectionFactory);
        container.setSubscriptionDurable(true);
        container.setDestination(new ActiveMQTopic(topic));
        container.setClientId(systemId + "." + authToken);
        container.setDurableSubscriptionName(topic + "." + systemId + "." + authToken);
        container.afterPropertiesSet();
        container.start();
        containers.put(subscriber, container);
    }

    @Override
    public void publish(MessageOutbox message) {
        try {
            jmsTemplate.convertAndSend(new ActiveMQTopic(topic), objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unsubscribe(Serializable subscriber) {
        DefaultMessageListenerContainer container = containers.remove(subscriber);
        if (container != null) {
            container.stop();
            container.shutdown();
        }
    }
}
