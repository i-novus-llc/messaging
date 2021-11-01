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
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageOutbox;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Api(value = "Уведомления", authorizations = @Authorization(value = "oauth2"))
@Path("/{tenantCode}/messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MessageRest {
    @GET
    @ApiOperation("Получение страницы уведомлений по критериям поиска")
    @ApiResponse(code = 200, message = "Страница уведомлений")
    Page<Message> getMessages(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                              @BeanParam @ApiParam(value = "Критерии уведомлений") MessageCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получение уведомления по идентификатору")
    @ApiResponse(code = 200, message = "Уведомление")
    Message getMessage(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                       @PathParam("id") @ApiParam(value = "Идентификатор уведомления") UUID id);

    @POST
    @ApiOperation("Отправка уведомления")
    @ApiResponse(code = 200, message = "Уведомление поставлено в очередь на отправку")
    void sendMessage(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                     @ApiParam(value = "Уведомление") MessageOutbox message);
}
