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

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.MessageSettingCriteria;
import ru.inovus.messaging.api.model.MessageSetting;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Api(value = "Шаблоны уведомлений", authorizations = @Authorization(value = "oauth2"))
@Path("/{tenantCode}/settings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MessageSettingRest {

    @GET
    @ApiOperation("Получение страницы шаблонов уведомлений по критериям поиска")
    @ApiResponse(code = 200, message = "Страница шаблонов уведомлений")
    Page<MessageSetting> getSettings(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                                     @BeanParam @ApiParam(value = "Критерии шаблонов уведомлений") MessageSettingCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получение шаблона уведомления по идентификатору")
    @ApiResponse(code = 200, message = "Шаблон уведомления")
    MessageSetting getSetting(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                              @PathParam("id") Integer id);

    @POST
    @ApiOperation("Создание шаблона уведомления")
    @ApiResponse(code = 200, message = "Шаблон уведомления успешно создан")
    void createSetting(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                       @ApiParam(value = "Шаблон уведомления") MessageSetting messageSetting);

    @PUT
    @Path("/{id}")
    @ApiOperation("Изменение шаблона уведомления")
    @ApiResponse(code = 200, message = "Шаблон уведомления успешно сохранен")
    void updateSetting(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                       @PathParam("id") @ApiParam(value = "Идентификатор шаблона уведомления") Integer id,
                       @ApiParam(value = "Шаблон уведомления") MessageSetting messageSetting);

    @DELETE
    @Path("/{id}")
    @ApiOperation("Удаление шаблона уведомления")
    @ApiResponse(code = 200, message = "Шаблон успешно удален")
    void deleteSetting(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                       @PathParam("id") @ApiParam(value = "Идентификатор шаблона уведомления") Integer id);
}
