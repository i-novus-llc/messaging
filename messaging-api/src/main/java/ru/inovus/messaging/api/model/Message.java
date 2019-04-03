package ru.inovus.messaging.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {

    private static final long serialVersionUID = 5475383823197483227L;

    private String id;
    private String caption;
    private String text;
    private Severity severity;
    private AlertType alertType;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private List<InfoType> infoTypes;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
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

    public List<InfoType> getInfoTypes() {
        return infoTypes;
    }

    public void setInfoTypes(List<InfoType> infoTypes) {
        this.infoTypes = infoTypes;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public FormationType getFormationType() {
        return formationType;
    }

    public void setFormationType(FormationType formationType) {
        this.formationType = formationType;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
