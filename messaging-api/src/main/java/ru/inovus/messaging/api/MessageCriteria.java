package ru.inovus.messaging.api;

import net.n2oapp.platform.jaxrs.RestCriteria;

import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;

public class MessageCriteria extends RestCriteria {

    private static final long serialVersionUID = 7609048158169451956L;

    @QueryParam("user")
    private String user;
    @QueryParam("systemId")
    private String systemId;
    @QueryParam("sentAt.begin")
    private LocalDateTime sentAtBegin;
    @QueryParam("sentAt.end")
    private LocalDateTime sentAtEnd;
    @QueryParam("severity.id")
    private Severity severity;
    @QueryParam("infoType.id")
    private InfoType infoType;
    @QueryParam("component.id")
    private Integer componentId;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public LocalDateTime getSentAtBegin() {
        return sentAtBegin;
    }

    public void setSentAtBegin(LocalDateTime sentAtBegin) {
        this.sentAtBegin = sentAtBegin;
    }

    public LocalDateTime getSentAtEnd() {
        return sentAtEnd;
    }

    public void setSentAtEnd(LocalDateTime sentAtEnd) {
        this.sentAtEnd = sentAtEnd;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public InfoType getInfoType() {
        return infoType;
    }

    public void setInfoType(InfoType infoType) {
        this.infoType = infoType;
    }

    public Integer getComponentId() {
        return componentId;
    }

    public void setComponentId(Integer componentId) {
        this.componentId = componentId;
    }
}
