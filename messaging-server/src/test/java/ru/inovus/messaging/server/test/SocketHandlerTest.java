package ru.inovus.messaging.server.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.inovus.messaging.api.*;
import ru.inovus.messaging.impl.MessageService;
import ru.inovus.messaging.server.MessagingApplication;
import ru.inovus.messaging.server.config.handler.SocketHandler;
import ru.inovus.messaging.server.model.SocketEvent;
import ru.inovus.messaging.server.model.SocketEventType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.inovus.messaging.server.config.handler.SocketHandler.isNotExpired;

//@SpringBootTest
//@RunWith(SpringRunner.class)
public class SocketHandlerTest {

    private MessageService messageService;
    private Boolean markedRead = false;
    private Boolean markedReadAll = false;
    private Boolean messageSent = false;
    private Boolean subscribed = false;
    private Boolean unsubscribed = false;
    private String messageId = "123";
    //    @Autowired
    private ObjectMapper objectMapper;
    private SocketHandler socketHandler;
    private MqProvider mqProvider;
    public static Map<String, String> headers = new HashMap<>();

    static {
        headers.put(SocketHandler.AUTH_TOKEN_HEADER, "1");
        headers.put(SocketHandler.SYSTEM_ID_HEADER, "default");
    }

    @Before
    public void init() {
        messageService = mock(MessageService.class);
        Message message = new Message();
        message.setId(messageId);
        doAnswer(invocation -> {
            markedRead = true;
            return null;
        }).when(messageService).markRead("default", messageId);
        doAnswer(invocation -> {
            markedReadAll = true;
            return null;
        }).when(messageService).markReadAll(any(), any());
        when(messageService.getUnreadMessages(any(), any())).thenReturn(new UnreadMessagesInfo(1));
        objectMapper = new MessagingApplication().objectMapper();
        mqProvider = mock(MqProvider.class);
        doAnswer(invocation -> {
            subscribed = true;
            return null;
        }).when(mqProvider).subscribe(any(), any(), any(), any());
        doAnswer(invocation -> {
            unsubscribed = true;
            return null;
        }).when(mqProvider).unsubscribe(any());
        socketHandler = new SocketHandler(objectMapper, mqProvider, messageService);
        socketHandler.setTimeout(60);
    }

    private WebSocketSession getWebSocketSession() throws IOException {
        WebSocketSession webSocketSession = mock(WebSocketSession.class);
        doAnswer(invocation -> {
            messageSent = true;
            return null;
        }).when(webSocketSession).sendMessage(any());
        return webSocketSession;
    }

    private SocketEvent getSocketEvent(SocketEventType type) {
        SocketEvent socketEvent = new SocketEvent();
        socketEvent.setHeaders(headers);
        socketEvent.setType(type);
        return socketEvent;
    }

    @Test
    public void testHandleEventRead() throws Exception {
        SocketEvent socketEvent = getSocketEvent(SocketEventType.READ);
        socketEvent.setMessage(new Message(messageId));
        socketHandler.handleMessage(getWebSocketSession(),
                new TextMessage(objectMapper.writeValueAsString(socketEvent)));
        assertTrue(markedRead);
        assertTrue(messageSent);
        assertFalse(markedReadAll);
    }

    @Test
    public void testHandleEventReadAll() throws Exception {
        SocketEvent socketEvent = getSocketEvent(SocketEventType.READ);
        socketHandler.handleMessage(getWebSocketSession(),
                new TextMessage(objectMapper.writeValueAsString(socketEvent)));
        assertTrue(markedReadAll);
        assertFalse(markedRead);
        assertTrue(messageSent);
    }

    @Test
    public void testHandleEventConnect() throws Exception {
        SocketEvent socketEvent = getSocketEvent(SocketEventType.CONNECT);
        socketHandler.handleMessage(getWebSocketSession(),
                new TextMessage(objectMapper.writeValueAsString(socketEvent)));
        assertFalse(markedReadAll);
        assertFalse(markedRead);
        assertTrue(messageSent);
        assertTrue(subscribed);
    }

    @Test
    public void testHandleEventCount() throws Exception {
        SocketEvent socketEvent = getSocketEvent(SocketEventType.COUNT);
        socketHandler.handleMessage(getWebSocketSession(),
                new TextMessage(objectMapper.writeValueAsString(socketEvent)));
        assertFalse(markedReadAll);
        assertFalse(markedRead);
        assertTrue(messageSent);
        assertFalse(subscribed);
    }

    @Test
    public void testAfterConnectionClosed() throws Exception {
        socketHandler.afterConnectionClosed(getWebSocketSession(), CloseStatus.NO_STATUS_CODE);
        assertTrue(unsubscribed);
    }

    private void mockPublish(String recipient, String systemId) {
        doAnswer(invocation -> {
            MessageOutbox messageOutbox = invocation.getArgument(0);
            socketHandler.sendTo(getWebSocketSession(), messageOutbox, recipient, systemId);
            return null;
        }).when(mqProvider).publish(any());
    }

    @Test
    public void testCheckRecipientAll() {
        mockPublish("1234", "default");
        MessageOutbox message = new MessageOutbox();
        message.setMessage(new Message());
        message.setRecipients(Collections.singletonList(new Recipient(RecipientType.ALL)));
        mqProvider.publish(message);
        assertTrue(messageSent); // message should be sent, because it was addressed to all users
    }

    @Test
    public void testCheckRecipientWrongUser() {
        mockPublish("1234", "default"); // <- wrong recipient
        MessageOutbox message = new MessageOutbox();
        message.setRecipients(Collections.singletonList(
                new Recipient(RecipientType.USER, "123", "default")));
        mqProvider.publish(message);                 // ^--- here's different user
        assertFalse(messageSent); // so message shouldn't be sent
    }

    @Test
    public void testCheckRecipientWrongSystemId() {
        mockPublish("1234", "foobar"); // <- wrong systemId
        MessageOutbox message = new MessageOutbox();
        message.setRecipients(Collections.singletonList(
                new Recipient(RecipientType.USER, "123", "default")));
        mqProvider.publish(message);    // here's different systemId -----^
        assertFalse(messageSent); // also message shouldn't be sent
    }

    @Test
    public void testCheckRecipientNoRecipients() {
        mockPublish("1234", "foobar");
        MessageOutbox message = new MessageOutbox();
        mqProvider.publish(message);
        assertFalse(messageSent); // no recipients
    }

    @Test
    public void testCheckRecipientOK() {
        mockPublish("123", "default"); // it' OK
        MessageOutbox message = new MessageOutbox();
        message.setMessage(new Message());
        message.setRecipients(Collections.singletonList(
                new Recipient(RecipientType.USER, "123", "default")));
        mqProvider.publish(message);
        assertTrue(messageSent); // message should be sent
    }

    @Test
    public void testExpiration() {
        assertTrue("Shouldn't be expired", isNotExpired(
                LocalDateTime.parse("2018-11-21T15:30:00"),
                LocalDateTime.parse("2018-11-21T15:30:59"),
                60));
        assertFalse("Should be expired", isNotExpired(
                LocalDateTime.parse("2018-11-21T15:30:00"),
                LocalDateTime.parse("2018-11-21T15:31:00"),
                60));
    }
}
