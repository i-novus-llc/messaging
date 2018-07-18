package ru.inovus.messaging.impl.rest;

import net.n2oapp.criteria.api.Criteria;

public class MessagingCriteria extends Criteria {
    private static final long serialVersionUID = 7609048158169451956L;

    private String user;
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
