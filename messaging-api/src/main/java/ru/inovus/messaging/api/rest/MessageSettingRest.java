/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.inovus.messaging.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.MessageSettingCriteria;
import ru.inovus.messaging.api.model.MessageSetting;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Api(value = "Шаблоны уведомлений", authorizations = @Authorization(value = "oauth2"))
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
