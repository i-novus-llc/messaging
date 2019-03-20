package ru.inovus.messaging.server.model;

import ru.inovus.messaging.api.model.Message;

import java.util.Map;

public class SocketEvent {
    private SocketEventType type;
    private Message message;
    private Map<String, String> headers;

    public SocketEventType getType() {
        return type;
    }

    public void setType(SocketEventType type) {
        this.type = type;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
