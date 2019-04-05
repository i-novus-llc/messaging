package ru.inovus.messaging.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.Recipient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Api("Получатели")
@Path("/recipients")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RecipientRest {
    @GET
    @ApiOperation("Получение получателей")
    @ApiResponse(code = 200, message = "Список получателей")
    Page<Recipient> getSettings(@BeanParam RecipientCriteria criteria);

}
