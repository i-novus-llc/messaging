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
import ru.inovus.messaging.api.criteria.FeedCriteria;
import ru.inovus.messaging.api.model.Feed;
import ru.inovus.messaging.api.model.FeedCount;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.UUID;

@Api(value = "Лента уведомлений", authorizations = @Authorization(value = "oauth2"))
@Path("/{tenantCode}/feed")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface FeedRest {
    @GET
    @Path("/{username}")
    @ApiOperation("Получение ленты уведомлений по критериям поиска")
    @ApiResponse(code = 200, message = "Страница уведомлений")
    Page<Feed> getMessageFeed(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                              @PathParam("username") @ApiParam(value = "Имя пользователя") String username,
                              @BeanParam @ApiParam(value = "Критерии ленты уведомлений") FeedCriteria criteria);

    @GET
    @Path("/{username}/count")
    @ApiOperation("Получение количества непрочитанных уведомлений")
    @ApiResponse(code = 200, message = "Количество непрочитанных уведомлений")
    FeedCount getFeedCount(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                           @PathParam("username") @ApiParam(value = "Имя пользователя") String username);

    @GET
    @Path("/{username}/message/{id}")
    @ApiOperation("Получение уведомления по идентификатору")
    @ApiResponse(code = 200, message = "Уведомление")
    Feed getMessage(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                    @PathParam("username") @ApiParam(value = "Имя пользователя") String username,
                    @PathParam("id") @ApiParam(value = "Идентификатор уведомления") UUID id);

    @GET
    @Path("/{username}/message/{id}/read")
    @ApiOperation("Получение уведомления по идентификатору и фиксирование даты прочтения")
    @ApiResponse(code = 200, message = "Уведомление")
    Feed getMessageAndRead(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                           @PathParam("username") @ApiParam(value = "Имя пользователя") String username,
                           @PathParam("id") @ApiParam(value = "Идентификатор уведомления") UUID id);

    @POST
    @Path("/{username}/readall")
    @ApiOperation("Пометить все уведомления прочитанными")
    @ApiResponse(code = 200, message = "Все уведомления помечены прочитанными")
    void markReadAll(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                     @PathParam("username") @ApiParam(value = "Имя пользователя") String username);

    @POST
    @Path("/{username}/message/{id}/read")
    @ApiOperation("Пометить уведомление прочитанным")
    @ApiResponse(code = 200, message = "Уведомление помечено прочитанными")
    void markRead(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                  @PathParam("username") @ApiParam(value = "Имя пользователя") String username,
                  @PathParam("id") @ApiParam(value = "Идентификатор уведомления") UUID id);
}
