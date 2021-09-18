package ru.inovus.messaging.web.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
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
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.QueueMqConsumer;
import ru.inovus.messaging.mq.support.kafka.KafkaMqProvider;
import ru.inovus.messaging.web.channel.configuration.WebSocketConfiguration;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({EmbeddedKafkaTestConfiguration.class, WebSocketConfiguration.class})
@EmbeddedKafka(partitions = 1)
@ContextConfiguration(classes = KafkaMqProvider.class)
public class WebChannelTest {
    @Autowired
    private MqProvider provider;

    @Autowired
    private WebChannel channel;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Value("${novus.messaging.queue.status}")
    private String statusQueue;

    @Value("${novus.messaging.channel.web.queue}")
    private String webQueue;

    @Value("${novus.messaging.channel.web.security.token}")
    private String token;

    @Value("${novus.messaging.channel.web.app_prefix}")
    private String appPrefix;

    @Value("${novus.messaging.channel.web.end_point}")
    private String endPoint;

    @Value("${novus.messaging.channel.web.private_dest_prefix}")
    private String privateDestPrefix;

    private static final String SYSTEM_ID = "system-id";
    private static final String USERNAME = "test-user";

    @LocalServerPort
    private Integer port;

    private String URL;

    private CompletableFuture<Object> completableFuture;

    private WebSocketStompClient stompClient;


    @BeforeEach
    public void init() {
        URL = "ws://localhost:" + port + endPoint + "?access_token=" + token;
        completableFuture = new CompletableFuture<>();

        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        stompClient = new WebSocketStompClient(new SockJsClient(transports));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void testSendMessageViaWebQueue() throws Exception {
        // publish session subscribe event
        UserPrincipal user = new UserPrincipal(USERNAME);
        org.springframework.messaging.Message<byte[]> sessionMessage =
                createMessage(SimpMessageType.CONNECT, "123", user,
                        "/user/" + USERNAME + "/exchange/" + SYSTEM_ID + "/message");
        SessionSubscribeEvent event = new SessionSubscribeEvent(this, sessionMessage, user);
        publisher.publishEvent(event);

        // create message
        Message message = new Message();
        message.setCaption("Test caption");
        message.setSeverity(Severity.ERROR);
        message.setText("Message");
        message.setSystemId(SYSTEM_ID);

        Recipient recipient1 = new Recipient();
        recipient1.setRecipientSendChannelId("test-user");
        Recipient recipient2 = new Recipient();
        recipient2.setRecipientSendChannelId("test-user2");
        message.setRecipients(Arrays.asList(recipient1, recipient2));

        // publish message to web queue and wait sending to stomp
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
        stompSession.subscribe("/user" + privateDestPrefix + "/" + SYSTEM_ID + "/message", new TestReceivedMessagesHandler());

        provider.publish(message, webQueue);

        Object receivedMessage = completableFuture.get(10, SECONDS);
        stompSession.disconnect();

        assertThat(receivedMessage, notNullValue());
        Assert.assertEquals("Test321", ((Message) receivedMessage).getId());
    }

    public void testSendMessageStatusSuccess() {

    }

    public void testSendMessageStatusFailed() {

    }

    public void testMarkReadMessage() throws Exception {
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
        assertThat(stompSession, notNullValue());

        CountDownLatch latch = new CountDownLatch(1);

        final MessageStatus[] receivedStatus = new MessageStatus[1];
        QueueMqConsumer mqConsumer = new QueueMqConsumer(statusQueue, msg -> {
            receivedStatus[0] = (MessageStatus) msg;
            latch.countDown();
        }, statusQueue);

        String messageId = "e8aa6312-c8ea-4dd6-a4a0-736f8856b348";
        Map<String, String> payload = Map.of("username", USERNAME, "messageId", messageId);

        provider.subscribe(mqConsumer);
        stompSession.send(appPrefix + "/" + SYSTEM_ID + "/message.markreadall", payload);
        latch.await();
        stompSession.disconnect();
        provider.unsubscribe(mqConsumer.subscriber());

        assertThat(receivedStatus[0], notNullValue());
        assertThat(receivedStatus[0].getUsername(), is(USERNAME));
        assertThat(receivedStatus[0].getStatus(), is(MessageStatusType.READ));
        assertThat(receivedStatus[0].getMessageId(), is(messageId));
        assertThat(receivedStatus[0].getSystemId(), is("system-id"));
    }

    @Test
    public void testMarkReadAllMessage() throws Exception {
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
        assertThat(stompSession, notNullValue());

        CountDownLatch latch = new CountDownLatch(1);

        final MessageStatus[] receivedStatus = new MessageStatus[1];
        QueueMqConsumer mqConsumer = new QueueMqConsumer(statusQueue, msg -> {
            receivedStatus[0] = (MessageStatus) msg;
            latch.countDown();
        }, statusQueue);

        Map<String, String> payload = Map.of("username", USERNAME);

        provider.subscribe(mqConsumer);
        stompSession.send(appPrefix + "/" + SYSTEM_ID + "/message.markreadall", payload);
        latch.await();
        stompSession.disconnect();
        provider.unsubscribe(mqConsumer.subscriber());

        assertThat(receivedStatus[0], notNullValue());
        assertThat(receivedStatus[0].getUsername(), is(USERNAME));
        assertThat(receivedStatus[0].getStatus(), is(MessageStatusType.READ));
        assertThat(receivedStatus[0].getSystemId(), is("system-id"));
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
            completableFuture.complete(payload);
        }
    }
}
