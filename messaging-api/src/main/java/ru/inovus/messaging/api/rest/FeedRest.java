package ru.inovus.messaging.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.UnreadMessagesInfo;
import ru.inovus.messaging.api.criteria.FeedCriteria;
import ru.inovus.messaging.api.model.Feed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    Feed getMessage(@PathParam("recipient") String recipient, @PathParam("id") String id);

    @GET
    @Path("/{recipient}/message/{id}/read")
    @ApiOperation("Получение сообщения по идентификатору и фиксрование даты прочтения")
    @ApiResponse(code = 200, message = "Сообщение")
    Feed getMessageAndRead(@PathParam("recipient") String recipient, @PathParam("id") String id);

    @POST
    @Path("/{recipient}/readall/{systemId}")
    @ApiOperation("Пометить все сообщения прочитанными")
    @ApiResponse(code = 200, message = "Все сообщения помечены прочитанными")
    void markReadAll(@PathParam("recipient") String recipient, @PathParam("systemId") String systemId);
}
