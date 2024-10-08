package ru.inovus.messaging.api.criteria;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import jakarta.ws.rs.QueryParam;

@Getter
@Setter
public class SecurityBaseCriteria extends BaseMessagingCriteria {
    @QueryParam("name")
    @ApiParam("Наименование")
    private String name;
}
