package ru.inovus.messaging.api.criteria;

import net.n2oapp.platform.jaxrs.RestCriteria;
import ru.inovus.messaging.api.model.*;

import javax.ws.rs.QueryParam;

public class MessageSettingCriteria extends RestCriteria {
    private static final long serialVersionUID = 7609048158169451957L;

    @QueryParam("component.id")
    private Integer componentId;

    @QueryParam("severity.id")
    private Severity severity;

    @QueryParam("alertType.id")
    private AlertType alertType;

    @QueryParam("infoType.id")
    private InfoType infoType;

    @QueryParam("name")
    private String name;

    @QueryParam("formationType.id")
    private FormationType formationType;

    @QueryParam("enabled.id")
    private YesNo enabled;

    @QueryParam("code")
    private String code;

    public Integer getComponentId() {
        return componentId;
    }

    public void setComponentId(Integer componentId) {
        this.componentId = componentId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormationType getFormationType() {
        return formationType;
    }

    public void setFormationType(FormationType formationType) {
        this.formationType = formationType;
    }

    public void setEnabled(YesNo enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnabled() {
        return enabled != null ? enabled.getValue() : null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
