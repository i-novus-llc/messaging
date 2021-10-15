package ru.inovus.messaging.api.rest;

import io.swagger.annotations.*;
import ru.inovus.messaging.api.model.Channel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Api(value = "Каналы", authorizations = @Authorization(value = "oauth2"))
@Path("/{tenantCode}/channels")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ChannelRest {

    @GET
    @ApiOperation("Получение списка каналов")
    @ApiResponse(code = 200, message = "Список каналов")
    List<Channel> getChannels(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode);

    @GET
    @ApiOperation("Получение канала по идентификатору")
    @ApiResponse(code = 200, message = "Канал")
    Channel getChannel(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                       @PathParam("id") @ApiParam(value = "Идентификатор канала") Integer id);
}

