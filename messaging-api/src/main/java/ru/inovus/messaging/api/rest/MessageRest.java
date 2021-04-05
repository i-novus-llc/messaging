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
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageOutbox;
import ru.inovus.messaging.api.model.Recipient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Api("Уведомления")
@Path("/messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MessageRest {
    @GET
    @ApiOperation("Получение страницы сообщений по критериям поиска")
    @ApiResponse(code = 200, message = "Страница сообщений")
    Page<Message> getMessages(@BeanParam MessageCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получение сообщения по идентификатору")
    @ApiResponse(code = 200, message = "Сообщение")
    Message getMessage(@PathParam("id") UUID id);

    @GET
    @Path("/recipients")
    @ApiOperation("Получение получателей")
    @ApiResponse(code = 200, message = "Список получателей")
    Page<Recipient> getRecipients(@BeanParam RecipientCriteria criteria);

    @POST
    @ApiOperation("Отправка сообщения")
    @ApiResponse(code = 200, message = "Сообщение поставлено в очередь")
    void sendMessage(MessageOutbox message);
}
