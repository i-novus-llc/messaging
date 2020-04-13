package ru.inovus.messaging.api.criteria;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.platform.jaxrs.RestCriteria;

import javax.ws.rs.QueryParam;

@Getter
@Setter
public class ComponentCriteria extends RestCriteria {
    //todo serialVersionUID

    @QueryParam("name")
    private String name;
}
