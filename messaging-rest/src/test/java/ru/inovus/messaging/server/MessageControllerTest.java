package ru.inovus.messaging.server;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.inovus.messaging.api.UnreadMessagesInfo;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.impl.FeedService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true",
                "spring.cloud.consul.config.enabled=false",
                "spring.liquibase.contexts=test", "sec.admin.rest.url=/",
                "novus.messaging.username.alias=preferred_username"})
@EnableEmbeddedPg
public class MessageControllerTest {

    private static final String SYSTEM_ID = "sysId900";

    private static final String TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJYUm5ONU01Y0pFMTlWNERtMnFuQ05KYWpwM0lsRGVYZl9QTk9fOWtKQXJvIn0." +
            "eyJqdGkiOiIxZDY2NGQ2Yi03YzdiLTQ0ODktYTM2Ni01ODBkMDUxYzkyZTQiLCJleHAiOjE1NTY1NjUyNzksIm5iZiI6MCwiaWF0IjoxNTU2NTI5Mjc5LCJpc3MiOiJodHRwOi8vMTI3" +
            "LjAuMC4xOjg4ODgvYXV0aC9yZWFsbXMvRE9NUkYiLCJhdWQiOiJsa2ItYXBwIiwic3ViIjoiOGJkZGFkNzItMTNiOC00ZjE4LTk0MDMtMWNiYTQ5OGVkOTczIiwidHlwIjoiQmVhcmVy" +
            "IiwiYXpwIjoibGtiLWFwcCIsImF1dGhfdGltZSI6MTU1NjUyOTI3OSwic2Vzc2lvbl9zdGF0ZSI6ImQwOTdmMjNkLWJjMzUtNGJkMS05OTgyLTM3NjllMjQ2MmU1ZiIsImFjciI6IjEi" +
            "LCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2RvY2tlci5vbmU6ODgwOC8qIiwiaHR0cDovL2xvY2FsaG9zdDo4MDgwLyoiLCJodHRwOi8vZG9ja2VyLm9uZTo4ODA1LyoiLCJodHRw" +
            "Oi8vZG9ja2VyLm9uZTo4ODAzLyoiLCJodHRwOi8vZG9ja2VyLm9uZTo4ODA0LyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInNlYy5hZG1pbiIsInJvbGUuYWNjcmVkaXRhdGlv" +
            "biIsInJkbS5hZG1pbiIsImFkbWluIiwiQURNSU4iLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmll" +
            "dy1pZGVudGl0eS1wcm92aWRlcnMiLCJ2aWV3LXJlYWxtIiwibWFuYWdlLWlkZW50aXR5LXByb3ZpZGVycyIsImltcGVyc29uYXRpb24iLCJyZWFsbS1hZG1pbiIsImNyZWF0ZS1jbGll" +
            "bnQiLCJtYW5hZ2UtdXNlcnMiLCJxdWVyeS1yZWFsbXMiLCJ2aWV3LWF1dGhvcml6YXRpb24iLCJxdWVyeS1jbGllbnRzIiwicXVlcnktdXNlcnMiLCJtYW5hZ2UtZXZlbnRzIiwibWFu" +
            "YWdlLXJlYWxtIiwidmlldy1ldmVudHMiLCJ2aWV3LXVzZXJzIiwidmlldy1jbGllbnRzIiwibWFuYWdlLWF1dGhvcml6YXRpb24iLCJtYW5hZ2UtY2xpZW50cyIsInF1ZXJ5LWdyb3Vw" +
            "cyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwicm9sZXMiOlsicmRtLmFkbWluIiwi" +
            "c2VjLmFkbWluIiwiQURNSU4iLCJ1bWFfYXV0aG9yaXphdGlvbiIsIm9mZmxpbmVfYWNjZXNzIiwiYWRtaW4iLCJyb2xlLmFjY3JlZGl0YXRpb24iXSwicHJlZmVycmVkX3VzZXJuYW1l" +
            "IjoibGtiIn0.KbZ65cwrbawTBNp-GZgqzoAq392Xr5dRHWubXOakgdH3nv8anbBSSocRoS2NuiDbpFXYG1hnOxHgK-6MBU_eDgjd2GrEMEzQrFrdTYvuqZSwFhuGekBSuPlizGv1tFgO" +
            "YjFoYLXpquNuV5L4rVrYMP3EIlfEDTOU_Puk8sZRX11uovKLWeHa71M-NaLzLY3-xlmux5V2l6XFBQqbUGkvOSEUO38mss26NZ3QTgXkY9Z9rZjW4NIGN8071Wdhr1NJ_uJ9RhlqjfrO" +
            "L0oJetlnRBfgEC_p6pgz1oWwntemTVtAX5SdLtNzQYleHXkixnU4FA0SOW2WNZ9MMQUwBsmMkQ";

    @Value("${novus.messaging.app_prefix}")
    private String appPrefix;
    @Value("${novus.messaging.end_point}")
    private String endPoint;
    @Value("${novus.messaging.private_dest_prefix}")
    private String privateDestPrefix;

    @LocalServerPort
    private Integer port;

    private String URL;

    private CompletableFuture<Object> completableFuture;

    private WebSocketStompClient stompClient;

    @MockBean
    public FeedService feedService;

    @Before
    public void init() {
        URL = "ws://localhost:" + port + endPoint + "?access_token=" + TOKEN;
        completableFuture = new CompletableFuture<>();

        stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void testMessageCount() throws Exception {
        Mockito.when(feedService.getFeedCount("lkb", SYSTEM_ID)).thenReturn(new UnreadMessagesInfo(99));

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        assertNotNull(stompSession);

        stompSession.subscribe("/user" + privateDestPrefix + "/" + SYSTEM_ID + "/message.count", new TestUnreadMessagesHandler());
        stompSession.send(appPrefix + "/" + SYSTEM_ID + "/message.count", null);

        Object result = completableFuture.get(10, SECONDS);
        stompSession.disconnect();

        assertNotNull(result);
        assertEquals(((UnreadMessagesInfo) result).getCount(), Integer.valueOf(99));
    }

    @Test
    public void testMarkReadAll() throws Exception {
        Mockito.doNothing().when(feedService).markReadAll(Mockito.isA(String.class), Mockito.isA(String.class));

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        assertNotNull(stompSession);
        stompSession.send(appPrefix + "/" + SYSTEM_ID + "/message.markreadall", null);
        stompSession.disconnect();

        Thread.sleep(500); //Сообщение должно дойти

        Mockito.verify(feedService, times(1)).markReadAll("lkb", SYSTEM_ID);
    }

    @Test
    public void testMarkReadList() throws Exception {
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        assertNotNull(stompSession);
        stompSession.send(appPrefix + "/" + SYSTEM_ID + "/message.markread", Arrays.asList(id1, id2, id3));
        stompSession.disconnect();

        Thread.sleep(500); //Сообщение должно дойти

        Mockito.verify(feedService, times(1)).markRead("lkb", id1, id2, id3);
    }

    @Test
    public void testSendReceiveMessage() throws Exception {
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        assertNotNull(stompSession);

        Message message = new Message("Test321");

        stompSession.subscribe("/user" + privateDestPrefix + "/" + SYSTEM_ID + "/message", new TestReceivedMessagesHandler());
        stompSession.send(appPrefix + "/" + SYSTEM_ID + "/message.private.lkb", message);

        Object result = completableFuture.get(10, SECONDS);
        stompSession.disconnect();

        assertNotNull(result);
        assertEquals("Test321", ((Message) result).getId());
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class TestUnreadMessagesHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return UnreadMessagesInfo.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            completableFuture.complete(payload);
        }
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
