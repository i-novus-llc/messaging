package ru.inovus.messaging.api;

import java.io.Serializable;
import java.util.List;

public class MessageOutbox implements Serializable {

    private static final long serialVersionUID = -5227708517651903498L;

    private Message message;
    private ControlMessage command;
    private List<String> recipients;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public ControlMessage getCommand() {
        return command;
    }

    public void setCommand(ControlMessage command) {
        this.command = command;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }
}
