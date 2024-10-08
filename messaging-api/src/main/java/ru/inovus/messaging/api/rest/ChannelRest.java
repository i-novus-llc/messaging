package ru.inovus.messaging.api.rest;

import io.swagger.annotations.*;
import ru.inovus.messaging.api.model.Channel;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Api(value = "Каналы", authorizations = @Authorization(value = "oauth2"))
@Path("/channels")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ChannelRest {

    @GET
    @ApiOperation("Получение списка каналов")
    @ApiResponse(code = 200, message = "Список каналов")
    List<Channel> getChannels();

    @GET
    @Path("/{code}")
    @ApiOperation("Получение канала по идентификатору")
    @ApiResponse(code = 200, message = "Канал")
    Channel getChannel(@PathParam("code") @ApiParam(value = "Идентификатор канала") String code);
}

