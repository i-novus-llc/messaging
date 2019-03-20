package ru.inovus.messaging.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
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
