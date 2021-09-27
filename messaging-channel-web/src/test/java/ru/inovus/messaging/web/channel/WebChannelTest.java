package ru.inovus.messaging.web.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
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
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.mq.support.kafka.KafkaMqProvider;
import ru.inovus.messaging.web.channel.configuration.WebSocketConfiguration;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({EmbeddedKafkaTestConfiguration.class, WebSocketConfiguration.class})
@EmbeddedKafka
@ContextConfiguration(classes = KafkaMqProvider.class)
public class WebChannelTest {
    @Autowired
    private MqProvider mqProvider;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Value("${novus.messaging.channel.web.queue}")
    private String webQueue;

    @Value("${novus.messaging.queue.feed-count}")
    private String feedCountQueue;

    @Value("${novus.messaging.security.token}")
    private String token;

    @Value("${novus.messaging.channel.web.end_point}")
    private String endPoint;

    @Value("${novus.messaging.channel.web.private_dest_prefix}")
    private String privateDestPrefix;

    private static final String SYSTEM_ID = "system-id";
    private static final String USERNAME = "lkb";

    @LocalServerPort
    private Integer port;

    private String URL;

    private CompletableFuture<Object> completableFuture;

    private WebSocketStompClient stompClient;

    private CountDownLatch latch;


    @BeforeEach
    public void init() {
        URL = "ws://localhost:" + port + endPoint + "?access_token=" + token;
        completableFuture = new CompletableFuture<>();

        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        stompClient = new WebSocketStompClient(new SockJsClient(transports));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void testSendMessage() throws Exception {
        // publish session subscribe event
        publisher.publishEvent(createSessionSubscribeEvent());

        // create message
        Message message = new Message();
        message.setCaption("Test caption");
        message.setSeverity(Severity.ERROR);
        message.setText("Message");
        message.setSystemId(SYSTEM_ID);
        Recipient recipient1 = new Recipient();
        recipient1.setUsername("test-user");
        Recipient recipient2 = new Recipient();
        recipient2.setUsername(USERNAME);
        message.setRecipients(Arrays.asList(recipient1, recipient2));

        // publish message to web queue and wait for sending to stomp
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
        stompSession.subscribe("/user" + privateDestPrefix + "/" + SYSTEM_ID + "/message", new TestReceivedMessageHandler());

        latch = new CountDownLatch(1);
        mqProvider.publish(message, webQueue);
        latch.await();

        // expected message on client
        Message receivedMessage = (Message) completableFuture.get();
        stompSession.disconnect();

        assertThat(receivedMessage.getCaption(), is(message.getCaption()));
        assertThat(receivedMessage.getText(), is(message.getText()));
        assertThat(receivedMessage.getSeverity(), is(message.getSeverity()));
    }

    @Test
    public void testSendFeedCount() throws Exception {
        // create feed count
        FeedCount feedCount = new FeedCount(SYSTEM_ID, USERNAME, 5);

        // publish message to feedCount queue and wait for sending to stomp
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
        stompSession.subscribe("/user" + privateDestPrefix + "/" + SYSTEM_ID + "/message.count", new TestReceivedFeedCountHandler());

        latch = new CountDownLatch(1);
        mqProvider.publish(feedCount, feedCountQueue);
        latch.await();

        // expected message on client
        Integer receivedFeedCount = (Integer) completableFuture.get();
        stompSession.disconnect();

        assertThat(receivedFeedCount, is(feedCount.getCount()));
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

    private class TestReceivedMessageHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            completableFuture.complete(payload);
            latch.countDown();
        }
    }

    private class TestReceivedFeedCountHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Integer.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            completableFuture.complete(payload);
            latch.countDown();
        }
    }
}
