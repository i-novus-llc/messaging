package ru.inovus.messaging.server.test;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.inovus.messaging.api.UnreadMessagesInfo;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.impl.FeedService;
import ru.inovus.messaging.server.BackendApplication;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BackendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true")
@EnableEmbeddedPg
public class MessageControllerTest {

    @LocalServerPort
    private Integer port;

    private String URL;

    private CompletableFuture<Object> completableFuture;

    private WebSocketStompClient stompClient;

    @MockBean
    public FeedService feedService;

    private static final String systemId = "sysId900";

    private static final String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJYUm5ONU01Y0pFMTlWNERtMnFuQ05KYWpwM0lsRGVYZl9QTk9fOWtKQXJvIn0."+
            "eyJqdGkiOiIxZDY2NGQ2Yi03YzdiLTQ0ODktYTM2Ni01ODBkMDUxYzkyZTQiLCJleHAiOjE1NTY1NjUyNzksIm5iZiI6MCwiaWF0IjoxNTU2NTI5Mjc5LCJpc3MiOiJodHRwOi8vMTI3LjAuMC4xOjg4ODgvYXV0aC9yZWFsbXMvRE9NUkYiLCJhdWQiOiJsa2ItYXBwIiwic3ViIjoiOGJkZGFkNzItMTNiOC00ZjE4LTk0MDMtMWNiYTQ5OGVkOTczIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibGtiLWFwcCIsImF1dGhfdGltZSI6MTU1NjUyOTI3OSwic2Vzc2lvbl9zdGF0ZSI6ImQwOTdmMjNkLWJjMzUtNGJkMS05OTgyLTM3NjllMjQ2MmU1ZiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2RvY2tlci5vbmU6ODgwOC8qIiwiaHR0cDovL2xvY2FsaG9zdDo4MDgwLyoiLCJodHRwOi8vZG9ja2VyLm9uZTo4ODA1LyoiLCJodHRwOi8vZG9ja2VyLm9uZTo4ODAzLyoiLCJodHRwOi8vZG9ja2VyLm9uZTo4ODA0LyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInNlYy5hZG1pbiIsInJvbGUuYWNjcmVkaXRhdGlvbiIsInJkbS5hZG1pbiIsImFkbWluIiwiQURNSU4iLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmlldy1pZGVudGl0eS1wcm92aWRlcnMiLCJ2aWV3LXJlYWxtIiwibWFuYWdlLWlkZW50aXR5LXByb3ZpZGVycyIsImltcGVyc29uYXRpb24iLCJyZWFsbS1hZG1pbiIsImNyZWF0ZS1jbGllbnQiLCJtYW5hZ2UtdXNlcnMiLCJxdWVyeS1yZWFsbXMiLCJ2aWV3LWF1dGhvcml6YXRpb24iLCJxdWVyeS1jbGllbnRzIiwicXVlcnktdXNlcnMiLCJtYW5hZ2UtZXZlbnRzIiwibWFuYWdlLXJlYWxtIiwidmlldy1ldmVudHMiLCJ2aWV3LXVzZXJzIiwidmlldy1jbGllbnRzIiwibWFuYWdlLWF1dGhvcml6YXRpb24iLCJtYW5hZ2UtY2xpZW50cyIsInF1ZXJ5LWdyb3VwcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwicm9sZXMiOlsicmRtLmFkbWluIiwic2VjLmFkbWluIiwiQURNSU4iLCJ1bWFfYXV0aG9yaXphdGlvbiIsIm9mZmxpbmVfYWNjZXNzIiwiYWRtaW4iLCJyb2xlLmFjY3JlZGl0YXRpb24iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoibGtiIn0."+
            "KbZ65cwrbawTBNp-GZgqzoAq392Xr5dRHWubXOakgdH3nv8anbBSSocRoS2NuiDbpFXYG1hnOxHgK-6MBU_eDgjd2GrEMEzQrFrdTYvuqZSwFhuGekBSuPlizGv1tFgOYjFoYLXpquNuV5L4rVrYMP3EIlfEDTOU_Puk8sZRX11uovKLWeHa71M-NaLzLY3-xlmux5V2l6XFBQqbUGkvOSEUO38mss26NZ3QTgXkY9Z9rZjW4NIGN8071Wdhr1NJ_uJ9RhlqjfrOL0oJetlnRBfgEC_p6pgz1oWwntemTVtAX5SdLtNzQYleHXkixnU4FA0SOW2WNZ9MMQUwBsmMkQ";


    @Before
    public void init() {
        URL = "ws://localhost:" + port + "/ws?access_token="+ token;
        completableFuture = new CompletableFuture<>();

        stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void testMessageCount() throws Exception {
        Mockito.when(feedService.getFeedCount("lkb", systemId)).thenReturn(new UnreadMessagesInfo(99));

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        assertNotNull(stompSession);

        stompSession.subscribe("/user/exchange/" + systemId + "/message.count", new TestUnreadMessagesHandler());
        stompSession.send("/app/" + systemId + "/message.count", null);

        Object result = completableFuture.get(10, SECONDS);
        stompSession.disconnect();

        assertNotNull(result);
        assertEquals(((UnreadMessagesInfo)result).getCount(), Integer.valueOf(99));
    }

    @Test
    public void testMarkReadAll() throws Exception {
        Mockito.doNothing().when(feedService).markReadAll(Mockito.isA(String.class), Mockito.isA(String.class));

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        assertNotNull(stompSession);
        stompSession.send("/app/" + systemId + "/message.markreadall", null);
        stompSession.disconnect();

        Thread.sleep(500); //Сообщение должно дойти

        Mockito.verify(feedService, times(1)).markReadAll("lkb", systemId);
    }

    @Test
    public void testMarkReadList() throws Exception {
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        assertNotNull(stompSession);
        stompSession.send("/app/" + systemId + "/message.markread", Arrays.asList("1","a2","Z"));
        stompSession.disconnect();

        Thread.sleep(500); //Сообщение должно дойти

        Mockito.verify(feedService, times(1)).markRead("lkb", "1","a2","Z");
    }

    @Test
    public void testSendReceiveMessage() throws Exception {
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        assertNotNull(stompSession);

        Message message = new Message("Test321");

        stompSession.subscribe("/user/exchange/" + systemId + "/message", new TestReceivedMessagesHandler());
        stompSession.send("/app/" + systemId + "/message.private.lkb", message);

        Object result = completableFuture.get(10, SECONDS);
        stompSession.disconnect();

        assertNotNull(result);
        assertEquals(((Message)result).getId(), "Test321");
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
