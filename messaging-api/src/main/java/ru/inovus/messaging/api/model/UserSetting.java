package ru.inovus.messaging.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSetting implements Serializable {

    private static final long serialVersionUID = 5475383823197483228L;

    private Integer id;
    private String caption;
    private String text;
    private Severity severity;
    private AlertType defaultAlertType;
    private AlertType alertType;
    private List<InfoType> defaultInfoType;
    private List<InfoType> infoTypes;
    private Component component;
    private String name;
    private Boolean disabled;
    private String templateCode;
    private boolean defaultSetting;

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

}
