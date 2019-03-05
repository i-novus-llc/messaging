package ru.inovus.messaging.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public InfoType getInfoType() {
        return infoType;
    }

    public void setInfoType(InfoType infoType) {
        this.infoType = infoType;
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

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    public List<String> getRecipientIds() {
        if (recipients == null)
            return null;
        return recipients.stream().map(Recipient::getRecipient)
                .collect(Collectors.toList());
    }

//    public String getRecipientString() {
//        if (recipients == null || recipients.isEmpty())
//            return "";
//        return recipients.stream().map(r -> r.)
//    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public Boolean getUnread() {
        return this.readAt == null;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
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
