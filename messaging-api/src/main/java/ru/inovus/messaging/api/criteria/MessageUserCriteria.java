package ru.inovus.messaging.api.criteria;

import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.QueryParam;

@Getter
@Setter
public class MessageUserCriteria extends MessageCriteria {

    @QueryParam("user")
    private String user;

}
