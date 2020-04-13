package ru.inovus.messaging.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.ComponentCriteria;
import ru.inovus.messaging.api.model.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Api("Компоненты системы")
@Path("/components")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ComponentRest {

    @GET
    @ApiOperation("Получение списка компонентов системы")
    @ApiResponse(code = 200, message = "Страница компонентов системы")
    Page<Component> getComponents(@BeanParam ComponentCriteria criteria);

}
