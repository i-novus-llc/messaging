package ru.inovus.messaging.api;

import net.n2oapp.platform.jaxrs.RestCriteria;

import javax.ws.rs.QueryParam;

public class MessagingCriteria extends RestCriteria {
    private static final long serialVersionUID = 7609048158169451956L;

    @QueryParam("user")
    private String user;
    @QueryParam("systemId")
    private String systemId;

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
}
