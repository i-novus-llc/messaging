package ru.inovus.messaging.api;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Recipient implements Serializable {

    private static final long serialVersionUID = 4960064989162334509L;

    private Integer id;
    private String recipient;
    private LocalDateTime readAt;
    private String messageId;
    private Integer userId;

    public Recipient() {
    }

    public Recipient(String recipient) {
        this.recipient = recipient;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}