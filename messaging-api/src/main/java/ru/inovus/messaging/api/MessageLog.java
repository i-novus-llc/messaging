package ru.inovus.messaging.api;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageLog implements Serializable {
    private static final long serialVersionUID = -8432832191820549052L;

    private Integer id;
    private Message message;
    private String recipient;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
