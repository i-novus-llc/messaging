package ru.inovus.messaging.server.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.inovus.messaging.api.*;
import ru.inovus.messaging.impl.MessageService;
import ru.inovus.messaging.server.config.ObjectSerializer;
import ru.inovus.messaging.server.model.SocketEvent;
import ru.inovus.messaging.server.model.SocketEventType;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);

    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, MessageListenerContainer> containers = new ConcurrentHashMap<>();
    private final KafkaProperties properties;
    private final Long timeout;
    private final String topic;
    private final String authHeaderName;
    private final MessageService messageService;

    public SocketHandler(KafkaProperties properties,
                         @Value("${novus.messaging.timeout}") Long timeout,
                         @Value("${novus.messaging.topic}") String topic,
                         @Value("${novus.messaging.auth-header-name}") String authHeaderName,
                         MessageService messageService) {
        mapper.registerModule(new JavaTimeModule());
        this.properties = properties;
        this.timeout = timeout;
        this.topic = topic;
        this.authHeaderName = authHeaderName;
        this.messageService = messageService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SocketEvent socketEvent = mapper.readValue(message.getPayload(), SocketEvent.class);
        handleEvent(session, socketEvent);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        MessageListenerContainer container = containers.remove(session.getId());
        if (container != null)
            container.stop();
    }

    private void handleEvent(WebSocketSession session, SocketEvent socketEvent) {
        String authToken = socketEvent.getHeaders().get(authHeaderName);
        if (SocketEventType.READ.equals(socketEvent.getType())) {
            if (socketEvent.getMessage() != null)
                messageService.markRead(socketEvent.getMessage().getId());
            else
                messageService.markReadAll(authToken);
        }
        UnreadMessagesInfo unreadMessages = messageService.getUnreadMessages(authToken);
        sendMessage(session, unreadMessages);
        if (SocketEventType.CONNECT.equals(socketEvent.getType())) {
            ContainerProperties containerProperties = new ContainerProperties(topic);
            containerProperties.setMessageListener((MessageListener<String, MessageOutbox>)
                    data -> sendTo(session, data.value(), authToken));
            Map<String, Object> consumerConfigs = getConsumerConfigs(authToken);
            containers.put(session.getId(), createContainer(consumerConfigs, containerProperties));
        }
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

    private Map<String, Object> getConsumerConfigs(String authToken) {
        Map<String, Object> consumerConfigs = properties.buildConsumerProperties();
        consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getConsumer().getGroupId() + "-" + authToken);
        consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ObjectSerializer.class);
        consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return consumerConfigs;
    }

    private boolean isNotExpired(MessageOutbox msg) {
        //todo
        return true; //msg.getMessage().getSentAt() == null ||
//                !msg.getMessage().getSentAt().plus(timeout, ChronoUnit.SECONDS)
//                        .isBefore(LocalDateTime.now(Clock.systemUTC()));
    }

    private boolean checkRecipient(MessageOutbox msg, String recipient) {
        if (msg == null || msg.getRecipients() == null || msg.getRecipients().isEmpty())
            return false;
        for (Recipient r : msg.getRecipients()) {
            if (r.getRecipientType() == RecipientType.ALL)
                return true;
            if (r.getUser().equals(recipient))
                return true;
        }
        return false;
    }

    private void sendMessage(WebSocketSession session, Object message) {
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendTo(WebSocketSession user, MessageOutbox msg, String recipient) {
        if (checkRecipient(msg, recipient)) {
            Message message = messageService.createMessage(msg.getMessage(), recipient);
            UnreadMessagesInfo unreadMessages = messageService.getUnreadMessages(recipient);
            sendMessage(user, unreadMessages);
            if (isNotExpired(msg)) {
                sendMessage(user, message);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Did not send message with id {} due to expiration {}",
                        message.getId(), message.getSentAt());
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug("No recipients for message");
        }
    }

}
