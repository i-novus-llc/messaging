package ru.inovus.messaging.channel.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.QueueMqConsumer;
import ru.inovus.messaging.mq.support.kafka.KafkaMqProvider;
import ru.inovus.messaging.channel.web.configuration.WebSocketConfiguration;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({EmbeddedKafkaTestConfiguration.class, WebSocketConfiguration.class})
@EmbeddedKafka(partitions = 1)
@ContextConfiguration(classes = KafkaMqProvider.class)
public class WebChannelStatusTest {
    @Autowired
    private MqProvider provider;

    @Autowired
    private WebChannel channel;

    @Autowired
    private ApplicationEventPublisher publisher;

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @Value("${novus.messaging.queue.status}")
    private String statusQueue;

    @Value("${novus.messaging.channel.web.queue:web-queue}")
    private String webQueue;

    @Value("${novus.messaging.security.token}")
    private String token;

    @Value("${novus.messaging.channel.web.app_prefix}")
    private String appPrefix;

    @Value("${novus.messaging.channel.web.end_point}")
    private String endPoint;

    @Value("${novus.messaging.channel.web.private_dest_prefix}")
    private String privateDestPrefix;

    private static final String SYSTEM_ID = "system-id";
    private static final String USERNAME = "lkb";

    @LocalServerPort
    private Integer port;

    private String URL;

    private WebSocketStompClient stompClient;

    private CountDownLatch latch;


    @BeforeEach
    public void init() {
        URL = "ws://localhost:" + port + endPoint + "?access_token=" + token;

        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        stompClient = new WebSocketStompClient(new SockJsClient(transports));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void testSendMessageStatusSuccess() throws Exception {
        // publish session subscribe event
        publisher.publishEvent(createSessionSubscribeEvent());

        // create message
        Message message = new Message();
        message.setId("6f711616-1617-11ec-9621-0242ac130003");
        message.setSystemId(SYSTEM_ID);
        message.setRecipients(Collections.singletonList(new Recipient(USERNAME)));

        final MessageStatus[] receivedStatus = new MessageStatus[1];
        QueueMqConsumer mqConsumer = new QueueMqConsumer(statusQueue, msg -> {
            receivedStatus[0] = (MessageStatus) msg;
            latch.countDown();
        }, statusQueue);

        // send message to web queue and wait for publishing to status queue
        provider.subscribe(mqConsumer);
        latch = new CountDownLatch(1);
        provider.publish(message, webQueue);
        latch.await();
        provider.unsubscribe(mqConsumer.subscriber());

        assertThat(receivedStatus[0], notNullValue());
        assertThat(receivedStatus[0].getUsername(), is(USERNAME));
        assertThat(receivedStatus[0].getMessageId(), is(message.getId()));
        assertThat(receivedStatus[0].getSystemId(), is(SYSTEM_ID));
        assertThat(receivedStatus[0].getStatus(), is(MessageStatusType.SENT));
    }

    @Test
    public void testSendMessageStatusFailed() throws Exception {
        // publish session subscribe event
        publisher.publishEvent(createSessionSubscribeEvent());

        // create message
        Message message = new Message();
        message.setId("6f711616-1617-11ec-9621-0242ac130003");
        message.setSystemId(SYSTEM_ID);
        message.setRecipients(Collections.singletonList(new Recipient(USERNAME)));

        final MessageStatus[] receivedStatus = new MessageStatus[1];
        QueueMqConsumer mqConsumer = new QueueMqConsumer(statusQueue, msg -> {
            receivedStatus[0] = (MessageStatus) msg;
            latch.countDown();
        }, statusQueue);

        String errorMessage = "Some exception message";
        doAnswer(a -> {
            throw new MessagingException(errorMessage);
        }).when(simpMessagingTemplate).convertAndSend(any(String.class), any(Object.class));

        // send message to web queue and wait for publishing to status queue
        provider.subscribe(mqConsumer);
        latch = new CountDownLatch(1);
        provider.publish(message, webQueue);
        latch.await();
        provider.unsubscribe(mqConsumer.subscriber());

        assertThat(receivedStatus[0], notNullValue());
        assertThat(receivedStatus[0].getUsername(), is(USERNAME));
        assertThat(receivedStatus[0].getMessageId(), is(message.getId()));
        assertThat(receivedStatus[0].getSystemId(), is(SYSTEM_ID));
        assertThat(receivedStatus[0].getStatus(), is(MessageStatusType.FAILED));
        assertThat(receivedStatus[0].getErrorMessage(), is(errorMessage));
    }

    @Test
    public void testMarkReadMessage() throws Exception {
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
        assertThat(stompSession, notNullValue());

        latch = new CountDownLatch(1);

        final MessageStatus[] receivedStatus = new MessageStatus[1];
        QueueMqConsumer mqConsumer = new QueueMqConsumer(statusQueue, msg -> {
            receivedStatus[0] = (MessageStatus) msg;
            latch.countDown();
        }, statusQueue);

        String messageId = "e8aa6312-c8ea-4dd6-a4a0-736f8856b348";

        // send message through ws and wait publishing to status queue
        provider.subscribe(mqConsumer);
        stompSession.send(appPrefix + "/" + SYSTEM_ID + "/message.markread", messageId);
        latch.await();
        stompSession.disconnect();
        provider.unsubscribe(mqConsumer.subscriber());

        assertThat(receivedStatus[0], notNullValue());
        assertThat(receivedStatus[0].getUsername(), is(USERNAME));
        assertThat(receivedStatus[0].getMessageId(), is(messageId));
        assertThat(receivedStatus[0].getSystemId(), is(SYSTEM_ID));
        assertThat(receivedStatus[0].getStatus(), is(MessageStatusType.READ));
    }

    @Test
    public void testMarkReadAllMessage() throws Exception {
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
        assertThat(stompSession, notNullValue());

        latch = new CountDownLatch(1);

        final MessageStatus[] receivedStatus = new MessageStatus[1];
        QueueMqConsumer mqConsumer = new QueueMqConsumer(statusQueue, msg -> {
            receivedStatus[0] = (MessageStatus) msg;
            latch.countDown();
        }, statusQueue);

        Map<String, String> payload = Map.of("username", USERNAME);

        // send message through ws and wait publishing to status queue
        provider.subscribe(mqConsumer);
        stompSession.send(appPrefix + "/" + SYSTEM_ID + "/message.markreadall", payload);
        latch.await();
        stompSession.disconnect();
        provider.unsubscribe(mqConsumer.subscriber());

        assertThat(receivedStatus[0], notNullValue());
        assertThat(receivedStatus[0].getUsername(), is(USERNAME));
        assertThat(receivedStatus[0].getSystemId(), is(SYSTEM_ID));
        assertThat(receivedStatus[0].getStatus(), is(MessageStatusType.READ));
    }


    private SessionSubscribeEvent createSessionSubscribeEvent() {
        UserPrincipal user = new UserPrincipal(USERNAME);
        org.springframework.messaging.Message<byte[]> sessionMessage =
                createMessage(SimpMessageType.CONNECT, "123", user,
                        "/user/" + USERNAME + "/exchange/" + SYSTEM_ID + "/message");
        return new SessionSubscribeEvent(this, sessionMessage, user);
    }

    private org.springframework.messaging.Message<byte[]> createMessage(SimpMessageType type, String sessionId,
                                                                        Principal user, String destination) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(type);
        accessor.setSessionId(sessionId);
        accessor.setUser(user);
        accessor.setDestination(destination);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    @Setter
    @Getter
    @AllArgsConstructor
    class UserPrincipal implements Principal {
        private String name;
    }

    private class TestReceivedMessagesHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            latch.countDown();
        }
    }
}
