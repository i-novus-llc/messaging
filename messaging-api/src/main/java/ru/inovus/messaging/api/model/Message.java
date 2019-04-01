package ru.inovus.messaging.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Message implements Serializable {

    private static final long serialVersionUID = 5475383823197483227L;

    private String id;
    private String caption;
    private String text;
    private Severity severity;
    private AlertType alertType;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private InfoType infoType;
    private Component component;
    private FormationType formationType;
    private RecipientType recipientType;
    private String systemId;
    private List<Recipient> recipients;
    private Map<String, String> data;

    public Message() {
    }

    public Message(String id) {
        this.id = id;
    }

    public String getSeverityName() {
        return this.severity != null ? severity.getName() : null;
    }

    public String getAlertTypeName() {
        return alertType != null ? alertType.getName() : null;
    }

    public String getInfoTypeName() {
        return infoType != null ? infoType.getName() : null;
    }

    public String getFormationTypeName() {
        return formationType != null ? formationType.getName() : null;
    }

    public String getRecipientTypeName() {
        return recipientType != null ? recipientType.getName() : null;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", caption='" + caption + '\'' +
                ", text='" + text + '\'' +
                ", severity=" + severity +
                ", alertType=" + alertType +
                ", sentAt=" + sentAt +
                ", readAt=" + readAt +
                ", data=" + data +
                '}';
    }
}
