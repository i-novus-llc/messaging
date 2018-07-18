package ru.inovus.messaging.api;

import java.io.Serializable;

public class Recipient implements Serializable {

    private static final long serialVersionUID = 4960064989162334509L;

    private RecipientType recipientType;
    private String user;
    private String systemId;

    public Recipient() {
    }

    public Recipient(RecipientType recipientType) {
        assert recipientType == RecipientType.ALL;
        this.recipientType = recipientType;
    }

    public Recipient(RecipientType recipientType, String user, String systemId) {
        this.recipientType = recipientType;
        this.user = user;
        this.systemId = systemId;
    }

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

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}
