package ru.inovus.messaging.api;

import java.io.Serializable;
import java.util.List;

public class MessageOutbox implements Serializable {

    private static final long serialVersionUID = -5227708517651903498L;

    private Message message;
    private List<Recipient> recipients;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }
}
