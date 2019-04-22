package ru.inovus.messaging.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Feed implements Serializable {

    private String id;
    private String caption;
    private String text;
    private Severity severity;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private Component component;
    private String systemId;

    public Feed() {
    }

    public Feed(String id) {
        this.id = id;
    }

    public String getSeverityName() {
        return this.severity != null ? severity.getName() : null;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", caption='" + caption + '\'' +
                ", text='" + text + '\'' +
                ", severity=" + severity +
                ", sentAt=" + sentAt +
                ", readAt=" + readAt +
                '}';
    }
}
