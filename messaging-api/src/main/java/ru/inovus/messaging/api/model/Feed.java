package ru.inovus.messaging.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}
