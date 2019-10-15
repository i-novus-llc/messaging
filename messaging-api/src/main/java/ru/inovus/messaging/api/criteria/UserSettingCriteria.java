package ru.inovus.messaging.api.criteria;

import net.n2oapp.platform.jaxrs.RestCriteria;
import ru.inovus.messaging.api.model.AlertType;
import ru.inovus.messaging.api.model.InfoType;
import ru.inovus.messaging.api.model.Severity;
import ru.inovus.messaging.api.model.YesNo;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class UserSettingCriteria extends RestCriteria {
    private static final long serialVersionUID = 7609048158169451958L;

    @PathParam("user")
    private String user;

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

    @QueryParam("templateCode")
    private String templateCode;

    @QueryParam("enabled.id")
    private YesNo enabled;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

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

    public Boolean getEnabled() {
        return enabled != null ? enabled.getValue() : null;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }
}
