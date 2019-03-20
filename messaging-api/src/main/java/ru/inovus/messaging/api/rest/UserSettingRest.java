package ru.inovus.messaging.api.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.UserSettingCriteria;
import ru.inovus.messaging.api.model.UserSetting;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/user/{user}/settings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserSettingRest {
    @GET
    @ApiOperation("Получение страницы пользовательских настроек по критериям поиска")
    @ApiResponse(code = 200, message = "Страница пользовательских настроек")
    Page<UserSetting> getSettings(@BeanParam UserSettingCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получение пользовательской настройки по идентификатору")
    @ApiResponse(code = 200, message = "Пользовательская настройка")
    UserSetting getSetting(@PathParam("user") String user, @PathParam("id") Integer id);

    @PUT
    @Path("/{id}")
    @ApiOperation("Изменение пользовательской настройки")
    @ApiResponse(code = 200, message = "Пользовательская настройка успешно изменена")
    void updateSetting(@PathParam("user") String user, @PathParam("id") Integer id, UserSetting messageSetting);

}
