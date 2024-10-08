package ru.inovus.messaging.channel.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.boot.stomp.N2oWebSocketController;
import net.n2oapp.framework.boot.stomp.WebSocketController;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.compile.pipeline.N2oPipelineSupport;
import net.n2oapp.framework.config.metadata.pack.*;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.N2oTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
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
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.api.rest.FeedRest;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.QueueMqConsumer;
import ru.inovus.messaging.channel.web.configuration.WebChannelProperties;
import ru.inovus.messaging.mq.support.kafka.KafkaMqProvider;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.*;
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
@EmbeddedKafka
@ContextConfiguration(classes = KafkaMqProvider.class)
public class WebChannelTest extends N2oTestBase {
    protected static final String TENANT_CODE = "tenant";
    protected static final String USERNAME = "test-user";

    @Autowired
    private MqProvider mqProvider;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Value("${novus.messaging.queue.feed-count}")
    private String feedCountQueue;
    @Autowired
    private WebChannelProperties properties;
    @Autowired
    private WebSocketController webSocketController;
    @Value("${novus.messaging.queue.status}")
    private String statusQueue;
    @MockBean
    private FeedRest feedRest;

    @LocalServerPort
    private Integer port;

    private String URL;

    private CompletableFuture<Object> completableFuture;

    private WebSocketStompClient stompClient;

    private CountDownLatch latch;

    @BeforeEach
    public void init() throws Exception {
        super.setUp();
        URL = "ws://localhost:" + port + properties.getEndPoint();
        completableFuture = new CompletableFuture<>();

        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        stompClient = new WebSocketStompClient(new SockJsClient(transports));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        builder.scan();
    }

    @Override
    protected final void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        configureEnvironment(builder);
    }

    protected void configureEnvironment(N2oApplicationBuilder builder) {
        MetadataEnvironment environment = builder.getEnvironment();
        ((N2oWebSocketController) webSocketController).setEnvironment(environment);
        ((N2oWebSocketController) webSocketController).setPipeline(N2oPipelineSupport.readPipeline(environment));
        builder.packs(new N2oPagesPack(), new N2oApplicationPack(), new N2oWidgetsPack(), new N2oFieldSetsPack(),
                new N2oControlsPack(), new N2oActionsPack());
        builder.sources(new CompileInfo("messaging.application.xml"));
    }

    @Test
    public void testSendMessage() throws Exception {
        // publish session subscribe event
        publisher.publishEvent(createSessionSubscribeEvent());

        // create message
        Message message = new Message();
        message.setId("72f643c6-e0fc-4c66-8a77-ea0e3652aa81");
        message.setCaption("Test caption");
        message.setText("Message");
        message.setSeverity(Severity.WARNING);
        message.setTenantCode(TENANT_CODE);
        Recipient recipient1 = new Recipient();
        recipient1.setUsername("test-user");
        Recipient recipient2 = new Recipient();
        recipient2.setUsername(USERNAME);
        message.setRecipients(Arrays.asList(recipient1, recipient2));

        latch = new CountDownLatch(2);
        QueueMqConsumer dummyConsumer = new QueueMqConsumer(statusQueue, msg -> latch.countDown(), statusQueue);

        // send message through ws and wait publishing to status queue
        mqProvider.subscribe(dummyConsumer);

        // publish message to web queue and wait for sending to stomp
        StompSession stompSession = getStompSessionWithHeaders();
        stompSession.subscribe("/user" + properties.getPrivateDestPrefix() + "/" + TENANT_CODE + "/message", new TestReceivedMessageHandler());

        mqProvider.publish(message, properties.getQueue());
        latch.await();
        mqProvider.unsubscribe(dummyConsumer.subscriber());

        // expected message on client
        Map receivedMessage = (Map) completableFuture.get();
        stompSession.disconnect();

        assertThat(getTitle(getPayloadData(receivedMessage)), is(message.getCaption()));
        assertThat(getPayloadData(receivedMessage).get("text"), is(message.getText()));
    }

    @Test
    public void testSendFeedCount() throws Exception {
        // create feed count
        FeedCount feedCount = new FeedCount(TENANT_CODE, USERNAME, 5);

        // publish message to feedCount queue and wait for sending to stomp
        StompSession stompSession = getStompSessionWithHeaders();
        subscribeFeedCountStompSession(stompSession);

        latch = new CountDownLatch(1);
        mqProvider.publish(feedCount, feedCountQueue);
        latch.await();

        // expected message on client
        Object receivedFeedCount = completableFuture.get();
        stompSession.disconnect();

        assertFeedCount(receivedFeedCount, feedCount);
    }

    @Test
    public void testSendMessageStatusSuccess() throws Exception {
        // publish session subscribe event
        publisher.publishEvent(createSessionSubscribeEvent());

        // create message
        Message message = new Message();
        message.setId("6f711616-1617-11ec-9621-0242ac130003");
        message.setTenantCode(TENANT_CODE);
        message.setSeverity(Severity.WARNING);
        message.setRecipients(Collections.singletonList(new Recipient(USERNAME)));

        final MessageStatus[] receivedStatus = new MessageStatus[1];
        QueueMqConsumer mqConsumer = new QueueMqConsumer(statusQueue, msg -> {
            receivedStatus[0] = (MessageStatus) msg;
            latch.countDown();
        }, statusQueue);

        // send message to web queue and wait for publishing to status queue
        mqProvider.subscribe(mqConsumer);
        latch = new CountDownLatch(1);
        mqProvider.publish(message, properties.getQueue());
        latch.await();
        mqProvider.unsubscribe(mqConsumer.subscriber());

        assertThat(receivedStatus[0], notNullValue());
        assertThat(receivedStatus[0].getUsername(), is(USERNAME));
        assertThat(receivedStatus[0].getMessageId(), is(message.getId()));
        assertThat(receivedStatus[0].getTenantCode(), is(TENANT_CODE));
        assertThat(receivedStatus[0].getStatus(), is(MessageStatusType.SENT));
    }

    @Test
    public void testMarkReadMessage() throws Exception {
        StompSession stompSession = getStompSessionWithHeaders();
        assertThat(stompSession, notNullValue());

        latch = new CountDownLatch(1);

        final MessageStatus[] receivedStatus = new MessageStatus[1];
        QueueMqConsumer mqConsumer = new QueueMqConsumer(statusQueue, msg -> {
            receivedStatus[0] = (MessageStatus) msg;
            latch.countDown();
        }, statusQueue);

        String messageId = "e8aa6312-c8ea-4dd6-a4a0-736f8856b348";

        // send message through ws and wait publishing to status queue
        mqProvider.subscribe(mqConsumer);
        stompSession.send(properties.getAppPrefix() + "/" + TENANT_CODE + "/message.markread", messageId);
        latch.await();
        stompSession.disconnect();
        mqProvider.unsubscribe(mqConsumer.subscriber());

        assertThat(receivedStatus[0], notNullValue());
        assertThat(receivedStatus[0].getUsername(), is(USERNAME));
        assertThat(receivedStatus[0].getMessageId(), is(messageId));
        assertThat(receivedStatus[0].getTenantCode(), is(TENANT_CODE));
        assertThat(receivedStatus[0].getStatus(), is(MessageStatusType.READ));
    }

    @Test
    public void testMarkReadAllMessage() throws Exception {
        StompSession stompSession = getStompSessionWithHeaders();
        assertThat(stompSession, notNullValue());

        latch = new CountDownLatch(1);

        final MessageStatus[] receivedStatus = new MessageStatus[1];
        QueueMqConsumer mqConsumer = new QueueMqConsumer(statusQueue, msg -> {
            receivedStatus[0] = (MessageStatus) msg;
            latch.countDown();
        }, statusQueue);

        Map<String, String> payload = Map.of("username", USERNAME);

        // send message through ws and wait publishing to status queue
        mqProvider.subscribe(mqConsumer);
        stompSession.send(properties.getAppPrefix() + "/" + TENANT_CODE + "/message.markreadall", payload);
        latch.await();
        stompSession.disconnect();
        mqProvider.unsubscribe(mqConsumer.subscriber());

        assertThat(receivedStatus[0], notNullValue());
        assertThat(receivedStatus[0].getUsername(), is(USERNAME));
        assertThat(receivedStatus[0].getTenantCode(), is(TENANT_CODE));
        assertThat(receivedStatus[0].getStatus(), is(MessageStatusType.READ));
    }

    protected String getTitle(Map map) {
        return ((String) map.get("title"));
    }

    protected Map getPayloadData(Map map) {
        return ((Map) ((ArrayList) (((Map) map.get("payload")).get("alerts"))).get(0));
    }

    protected void subscribeFeedCountStompSession(StompSession stompSession) {
        stompSession.subscribe("/user" + properties.getPrivateDestPrefix() + "/" + TENANT_CODE + "/message.count", new TestReceivedMessageHandler());
    }

    protected void assertFeedCount(Object receivedFeedCount, FeedCount feedCount) {
        assertThat(getPayloadData((Map) receivedFeedCount).get("text"), is(feedCount.getCount().toString()));
    }

    private StompSession getStompSessionWithHeaders() throws InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("username", USERNAME);
        return stompClient.connect(URL, new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
    }

    private SessionSubscribeEvent createSessionSubscribeEvent() {
        UserPrincipal user = new UserPrincipal(USERNAME);
        org.springframework.messaging.Message<byte[]> sessionMessage =
                createMessage(SimpMessageType.CONNECT, "123", user,
                        "/user/" + USERNAME + "/exchange/" + TENANT_CODE + "/message");
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

    protected class TestReceivedMessageHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Map.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            completableFuture.complete(payload);
            latch.countDown();
        }
    }

}
