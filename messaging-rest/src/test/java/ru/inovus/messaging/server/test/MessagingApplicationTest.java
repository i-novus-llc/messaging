package ru.inovus.messaging.server.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.jooq.DSLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import ru.inovus.messaging.client.MessagingClient;
import ru.inovus.messaging.impl.rest.MessagingResponse;
import ru.inovus.messaging.server.MessageController;
import ru.inovus.messaging.server.MessagingApplication;
import ru.inovus.messaging.server.model.SocketEvent;
import ru.inovus.messaging.server.model.SocketEventType;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MessagingApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableEmbeddedPg
public class MessagingApplicationTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private DSLContext dsl;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MessageController controller;

    private WebSocketClient wsClient = new StandardWebSocketClient();
    private MessagingClient messagingClient;

    @Before
    public void init() {

    }

    private void sendConnect(WebSocketSession session) throws Exception {
        SocketEvent socketEvent = new SocketEvent();
        socketEvent.setHeaders(SocketHandlerTest.headers);
        socketEvent.setType(SocketEventType.CONNECT);
        session.sendMessage(new TextMessage(mapper.writeValueAsString(socketEvent)));
    }

    @Test
    public void testConnect() throws Exception {
        System.out.println("port is : " + port);
        CompletableFuture<MessagingResponse> task = new CompletableFuture<>();

        ListenableFuture<WebSocketSession> future = wsClient.doHandshake(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                sendConnect(session);
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                task.complete(mapper.readValue(((TextMessage) message).getPayload(), MessagingResponse.class));
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                System.out.println("ERROR!!!!!");
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

            }

            @Override
            public boolean supportsPartialMessages() {
                return true;
            }
        }, "ws://localhost:{port}/ws", port);
        WebSocketSession webSocketSession = future.get();
        assertNotNull(webSocketSession);
        assertTrue(webSocketSession.isOpen());
        MessagingResponse response = task.get(5, TimeUnit.SECONDS);
        assertEquals((Integer) 0, response.getCount());
        webSocketSession.close();
    }
}
