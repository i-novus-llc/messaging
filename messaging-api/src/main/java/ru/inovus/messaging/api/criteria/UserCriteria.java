package ru.inovus.messaging.api.criteria;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.platform.jaxrs.RestCriteria;

import javax.ws.rs.QueryParam;

@Getter
@Setter
public class UserCriteria extends RestCriteria {

    @QueryParam("username")
    private String username;

    @QueryParam("name")
    private String name;
}
