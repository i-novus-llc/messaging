package ru.inovus.messaging.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.data.domain.Page;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface MessageRest {
    @GET
    @ApiOperation("Получение страницы сообщений по критериям поиска")
    @ApiResponse(code = 200, message = "Страница сообщений")
    Page<Message> getMessages(@BeanParam MessagingCriteria criteria);

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
    @Path("/{id}/read")
    @ApiOperation("Пометить сообщение прочитанным")
    @ApiResponse(code = 200, message = "Сообщение помечено прочитанным")
    void markRead(@PathParam("id") String id);
}