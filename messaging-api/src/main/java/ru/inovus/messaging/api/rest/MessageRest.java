package ru.inovus.messaging.api.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    Message getMessage(@PathParam("id") String id);

    @POST
    @ApiOperation("Отправка сообщения")
    @ApiResponse(code = 200, message = "Сообщение поставлено в очередь")
    void sendMessage(MessageOutbox message);

    @POST
    @Path("/{recipient}/read/{id}")
    @ApiOperation("Пометить сообщение прочитанным")
    @ApiResponse(code = 200, message = "Сообщение помечено прочитанным")
    void markRead(@PathParam("recipient") String recipient, @PathParam("id") String id);
}
