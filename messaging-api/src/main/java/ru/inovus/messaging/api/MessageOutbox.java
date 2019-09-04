package ru.inovus.messaging.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.TemplateMessageOutbox;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageOutbox implements Serializable {

    private static final long serialVersionUID = -5227708517651903498L;

    private Message message;
    private TemplateMessageOutbox templateMessageOutbox;

    public MessageOutbox() {
        //
    }

    public MessageOutbox(Message message, TemplateMessageOutbox messageOutbox){
        this.message = message;
        this.templateMessageOutbox = getTemplateMessageOutbox();
    }

    public MessageOutbox(Message message) {
        this.message = message;
    }

    public MessageOutbox(TemplateMessageOutbox templateMessageOutbox) {
        this.templateMessageOutbox = templateMessageOutbox;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public TemplateMessageOutbox getTemplateMessageOutbox() {
        return templateMessageOutbox;
    }

    public void setTemplateMessageOutbox(TemplateMessageOutbox templateMessageOutbox) {
        this.templateMessageOutbox = templateMessageOutbox;
    }
}
