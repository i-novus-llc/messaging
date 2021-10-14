package ru.inovus.messaging.api.rest;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.Recipient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Api(value = "Получатели уведомлений", authorizations = @Authorization(value = "oauth2"))
@Path("/recipients")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RecipientRest {

    @GET
    @Path("/")
    @ApiOperation("Получение страницы получателей по критериям поиска")
    @ApiResponse(code = 200, message = "Страница получателей")
    Page<Recipient> getRecipients(@BeanParam @ApiParam(value = "Критерии получателей") RecipientCriteria criteria);
}
