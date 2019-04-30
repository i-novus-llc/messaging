package ru.inovus.messaging.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.inovus.messaging.api.model.Message;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageOutbox implements Serializable {

    private static final long serialVersionUID = -5227708517651903498L;

    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

}
