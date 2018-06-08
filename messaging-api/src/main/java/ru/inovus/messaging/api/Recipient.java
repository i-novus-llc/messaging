package ru.inovus.messaging.api;

import java.io.Serializable;

public class Recipient implements Serializable {

    private static final long serialVersionUID = 4960064989162334509L;

    public Recipient() {
    }

    public Recipient(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public Recipient(RecipientType recipientType, String user) {
        this.recipientType = recipientType;
        this.user = user;
    }

    private RecipientType recipientType;
    private String user;

    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
