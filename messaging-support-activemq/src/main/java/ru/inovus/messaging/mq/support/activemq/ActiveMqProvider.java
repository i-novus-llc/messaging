package ru.inovus.messaging.mq.support.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.queue.MqConsumer;
import ru.inovus.messaging.api.queue.MqProvider;
import ru.inovus.messaging.api.queue.QueueMqConsumer;
import ru.inovus.messaging.api.queue.TopicMqConsumer;

import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ActiveMqProvider implements MqProvider {

    private static final String INITIAL_REDELIVERY_DELAY = "novus.messaging.redelivery-policy.%s.initial-redelivery-delay";
    private static final String REDELIVERY_DELAY = "novus.messaging.redelivery-policy.%s.redelivery-delay";
    private static final String MAXIMUM_REDELIVERIES = "novus.messaging.redelivery-policy.%s.maximum-redeliveries";
    private static final String USE_EXPONENTIAL_BACK_OFF = "novus.messaging.redelivery-policy.%s.use-exponential-back-off";
    private static final String BACK_OFF_MULTIPLIER = "novus.messaging.redelivery-policy.%s.back-off-multiplier";

    @Autowired
    private Environment environment;

    private final ObjectMapper objectMapper;
    private final ActiveMQConnectionFactory activeMQConnectionFactory;
    private final JmsTemplate jmsTemplate;
    private final Map<Serializable, DefaultMessageListenerContainer> containers = new ConcurrentHashMap<>();
    private final ActiveMQQueue emailQueue;
    private final ActiveMQTopic noticeTopic;

    private final Boolean durable;

    public ActiveMqProvider(ObjectMapper objectMapper,
                            @Value("${spring.activemq.broker-url}") String brokerUrl,
                            @Value("${novus.messaging.topic.notice}") String noticeTopicName,
                            @Value("${novus.messaging.topic.email}") String emailQueueName,
                            @Value("${novus.messaging.durable}") Boolean durable) {
        this.objectMapper = objectMapper;
        this.durable = durable;
        activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        this.jmsTemplate = new JmsTemplate(new CachingConnectionFactory(activeMQConnectionFactory));
        this.emailQueue = new ActiveMQQueue(emailQueueName);
        this.noticeTopic = new ActiveMQTopic(noticeTopicName);
    }

    @Override
    public void subscribe(MqConsumer mqConsumer) {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setMessageListener((MessageListener) message -> {
            try {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    MessageOutbox messageOutbox = objectMapper.readValue(text, MessageOutbox.class);
                    mqConsumer.messageHandler().accept(messageOutbox);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        container.setConnectionFactory(activeMQConnectionFactory);
        if (mqConsumer instanceof QueueMqConsumer) {
            ActiveMQQueue activeMQQueue = new ActiveMQQueue(mqConsumer.mqName());
            container.setDestination(activeMQQueue);

            container.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);

            setRedeliveryPolicy(activeMQQueue, mqConsumer.mqName());
        } else
        if (mqConsumer instanceof TopicMqConsumer) {
            TopicMqConsumer topicMqHandler = (TopicMqConsumer) mqConsumer;
            container.setDestination(new ActiveMQTopic(topicMqHandler.mqName()));
            container.setPubSubDomain(true);
            if (Boolean.TRUE.equals(durable)) {
                container.setSubscriptionDurable(true);
                container.setClientId(topicMqHandler.systemId + "." + topicMqHandler.authToken + "." + System.currentTimeMillis());
                container.setDurableSubscriptionName(topicMqHandler.mqName() + "." + topicMqHandler.systemId + "." + topicMqHandler.authToken);
            }
        }

        container.afterPropertiesSet();
        container.start();
        containers.put(mqConsumer.subscriber(), container);
    }

    private void setRedeliveryPolicy(ActiveMQQueue activeMQQueue, String mqName) {
        RedeliveryPolicy emailRedeliveryPolicy = new RedeliveryPolicy();

        Long initialRedeliveryDelay = environment.getProperty(String.format(INITIAL_REDELIVERY_DELAY, mqName), Long.class);
        if (initialRedeliveryDelay != null) {
            emailRedeliveryPolicy.setInitialRedeliveryDelay(initialRedeliveryDelay);
        }

        Long redeliveryDelay = environment.getProperty(String.format(REDELIVERY_DELAY, mqName), Long.class);
        if (redeliveryDelay != null) {
            emailRedeliveryPolicy.setRedeliveryDelay(redeliveryDelay);
        }

        Integer maximumRedeliveries = environment.getProperty(String.format(MAXIMUM_REDELIVERIES, mqName), Integer.class);
        if (maximumRedeliveries != null) {
            emailRedeliveryPolicy.setMaximumRedeliveries(maximumRedeliveries);
        }

        Boolean useExponentialBackOff = environment.getProperty(String.format(USE_EXPONENTIAL_BACK_OFF, mqName), Boolean.class);
        if (useExponentialBackOff != null) {
            emailRedeliveryPolicy.setUseExponentialBackOff(useExponentialBackOff);
        }

        Double backOffMultiplier = environment.getProperty(String.format(BACK_OFF_MULTIPLIER, mqName), Double.class);
        if (backOffMultiplier != null) {
            emailRedeliveryPolicy.setBackOffMultiplier(backOffMultiplier);
        }

        RedeliveryPolicyMap map =  activeMQConnectionFactory.getRedeliveryPolicyMap();
        map.put(activeMQQueue, emailRedeliveryPolicy);
    }

    @Override
    public void publish(MessageOutbox message, String mqDestinationName) {
        send(message, ActiveMQDestination.createDestination(mqDestinationName, ActiveMQDestination.TOPIC_TYPE));
    }

    @Override
    public void unsubscribe(Serializable subscriber) {
        DefaultMessageListenerContainer container = containers.remove(subscriber);
        if (container != null) {
            container.stop();
            container.shutdown();
        }
    }

    /**
     * Отправка в очередь нового сообщения
     *
     * @param message сообщение
     * @param destination топик или очередь
     */
    private void send(MessageOutbox message, Destination destination) {
        try {
            jmsTemplate.convertAndSend(destination, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
