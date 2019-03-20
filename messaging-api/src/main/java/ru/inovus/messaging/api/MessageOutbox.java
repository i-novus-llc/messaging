package ru.inovus.messaging.api;

import ru.inovus.messaging.api.model.Message;

import java.io.Serializable;

public class MessageOutbox implements Serializable {

    private static final long serialVersionUID = -5227708517651903498L;

    private Message message;
    private ControlMessage command;

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

}
