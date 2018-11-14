package ru.inovus.messaging.api;

import java.util.List;

public class ControlMessage {

    private ControlCommand command;

    private List<String> messageIds;

    public ControlCommand getCommand() {
        return command;
    }

    public void setCommand(ControlCommand command) {
        this.command = command;
    }

    public List<String> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(List<String> messageIds) {
        this.messageIds = messageIds;
    }
}
