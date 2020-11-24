package ru.inovus.messaging.api.criteria;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.QueryParam;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class RoleCriteria extends BaseMessagingCriteria {
    @QueryParam("name")
    private String name;

    @QueryParam("permissionCodes")
    private List<String> permissionCodes;
}
