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
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.FeedCriteria;
import ru.inovus.messaging.api.model.Feed;
import ru.inovus.messaging.api.model.UnreadMessagesInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Api("Лента уведомлений")
@Path("/feed")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface  FeedRest {
    @GET
    @Path("/{recipient}")
    @ApiOperation("Получение ленты уведомлений")
    @ApiResponse(code = 200, message = "Страница сообщений")
    Page<Feed> getMessageFeed(@PathParam("recipient") String recipient, @BeanParam FeedCriteria criteria);

    @GET
    @Path("/{recipient}/count/{systemId}")
    @ApiOperation("Получение количества не прочитанных уведомлений")
    @ApiResponse(code = 200, message = "Количество не прочитанных уведомлений")
    UnreadMessagesInfo getFeedCount(@PathParam("recipient") String recipient, @PathParam("systemId") String systemId);

    @GET
    @Path("/{recipient}/message/{id}")
    @ApiOperation("Получение сообщения по идентификатору")
    @ApiResponse(code = 200, message = "Сообщение")
    Feed getMessage(@PathParam("recipient") String recipient, @PathParam("id") UUID id);

    @GET
    @Path("/{recipient}/message/{id}/read")
    @ApiOperation("Получение сообщения по идентификатору и фиксирование даты прочтения")
    @ApiResponse(code = 200, message = "Сообщение")
    Feed getMessageAndRead(@PathParam("recipient") String recipient, @PathParam("id") UUID id);

    @POST
    @Path("/{recipient}/readall/{systemId}")
    @ApiOperation("Пометить все сообщения прочитанными")
    @ApiResponse(code = 200, message = "Все сообщения помечены прочитанными")
    void markReadAll(@PathParam("recipient") String recipient, @PathParam("systemId") String systemId);
}
