package ru.inovus.messaging.impl.rest;

import net.n2oapp.criteria.api.Criteria;

public class MessagingCriteria extends Criteria {
    private static final long serialVersionUID = 7609048158169451956L;

    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
