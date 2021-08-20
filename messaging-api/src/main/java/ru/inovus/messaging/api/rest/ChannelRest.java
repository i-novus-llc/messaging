package ru.inovus.messaging.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import ru.inovus.messaging.api.model.ChannelType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Api(value = "Каналы", authorizations = @Authorization(value = "oauth2"))
@Path("/channels")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ChannelRest {

    @GET
    @ApiOperation("Получение списка каналов")
    @ApiResponse(code = 200, message = "Список каналов")
    List<ChannelType> getChannels();

    @GET
    @ApiOperation("Получение канала по идентификатору")
    @ApiResponse(code = 200, message = "Канал")
    ChannelType getChannel(@PathParam("id") String id);
}

