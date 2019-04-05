package ru.inovus.messaging.api;

import ru.inovus.messaging.api.model.*;

import java.io.Serializable;
import java.util.List;

public class MessageSetting implements Serializable {

    private static final long serialVersionUID = 5475383823197483227L;

    private Integer id;
    private String caption;
    private String text;
    private Severity severity;
    private AlertType alertType;
    private List<InfoType> infoType;
    private FormationType formationType;
    private Component component;
    private String name;
    private Boolean disabled;

    public MessageSetting() {
    }

    public MessageSetting(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public List<InfoType> getInfoType() {
        return infoType;
    }

    public void setInfoType(List<InfoType> infoType) {
        this.infoType = infoType;
    }

    public FormationType getFormationType() {
        return formationType;
    }

    public void setFormationType(FormationType formationType) {
        this.formationType = formationType;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
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
}
