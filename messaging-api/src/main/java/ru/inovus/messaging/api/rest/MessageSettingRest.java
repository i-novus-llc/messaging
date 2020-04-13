package ru.inovus.messaging.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.MessageSetting;
import ru.inovus.messaging.api.criteria.MessageSettingCriteria;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Api("Шаблоны уведомлений")
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
    MessageSetting getSetting(@PathParam("id") Integer id);

    @POST
    @ApiOperation("Создание шаблона")
    @ApiResponse(code = 200, message = "Шаблон успешно создан")
    void createSetting(MessageSetting messageSetting);

    @PUT
    @Path("/{id}")
    @ApiOperation("Изменение шаблона")
    @ApiResponse(code = 200, message = "Шаблон успешно сохранен")
    void updateSetting(@PathParam("id") Integer id, MessageSetting messageSetting);

    @DELETE
    @Path("/{id}")
    @ApiOperation("Удаление шаблона")
    @ApiResponse(code = 200, message = "Шаблон успешно удален")
    void deleteSetting(@PathParam("id") Integer id);

}
