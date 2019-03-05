package ru.inovus.messaging.api;

import java.io.Serializable;

public class UserSetting implements Serializable {

    private static final long serialVersionUID = 5475383823197483228L;

    private Integer id;
    private String caption;
    private String text;
    private Severity severity;
    private AlertType defaultAlertType;
    private AlertType alertType;
    private InfoType defaultInfoType;
    private InfoType infoType;
    private Component component;
    private String name;
    private Boolean disabled;

    public UserSetting() {
    }

    public UserSetting(Integer id) {
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

    public InfoType getInfoType() {
        return infoType;
    }

    public void setInfoType(InfoType infoType) {
        this.infoType = infoType;
    }

    public AlertType getDefaultAlertType() {
        return defaultAlertType;
    }

    public void setDefaultAlertType(AlertType defaultAlertType) {
        this.defaultAlertType = defaultAlertType;
    }

    public InfoType getDefaultInfoType() {
        return defaultInfoType;
    }

    public void setDefaultInfoType(InfoType defaultInfoType) {
        this.defaultInfoType = defaultInfoType;
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

    public String getDefaultAlertTypeName() {
        return defaultAlertType != null ? defaultAlertType.getName() : null;
    }

    public String getInfoTypeName() {
        return infoType != null ? infoType.getName() : null;
    }

    public String getDefaultInfoTypeName() {
        return defaultInfoType != null ? defaultInfoType.getName() : null;
    }

}
