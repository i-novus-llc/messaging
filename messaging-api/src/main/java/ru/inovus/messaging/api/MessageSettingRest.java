package ru.inovus.messaging.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/settings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MessageSettingRest {
    @GET
    @ApiOperation("Получение страницы шаблонов сообщений по критериям поиска")
    @ApiResponse(code = 200, message = "Страница шаблонов сообщений")
    Page<MessageSetting> getSettings(@BeanParam MessageSettingCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получение шаблона по идентификатору")
    @ApiResponse(code = 200, message = "Шаблон сообщения")
    MessageSetting geSetting(@PathParam("id") String id);


}
