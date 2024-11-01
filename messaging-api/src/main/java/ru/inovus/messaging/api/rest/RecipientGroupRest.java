package ru.inovus.messaging.api.rest;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.RecipientGroupCriteria;
import ru.inovus.messaging.api.model.RecipientGroup;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Api(value = "Группы получателей", authorizations = @Authorization(value = "oauth2"))
@Path("/{tenantCode}/recipient-groups")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RecipientGroupRest {

    @GET
    @ApiOperation("Получение страницы 'группы получателей' по критериям поиска")
    @ApiResponse(code = 200, message = "Страница 'Группы получателей'")
    Page<RecipientGroup> getRecipientGroups(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                                            @BeanParam @ApiParam(value = "Критерии группы получателей") RecipientGroupCriteria criteria);

    @GET
    @Path("/{id}")
    @ApiOperation("Получение группы получателей по ID")
    @ApiResponse(code = 200, message = "Группа получателей")
    RecipientGroup getRecipientGroup(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                                     @PathParam("id") Integer id);

    @POST
    @ApiOperation("Создание группы получателей")
    @ApiResponse(code = 200, message = "Группа получателей успешно создан")
    void createRecipientGroup(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                              @ApiParam(value = "Группа получателей") RecipientGroup recipientGroup);

    @PUT
    @Path("/{id}")
    @ApiOperation("Изменение группы получателей")
    @ApiResponse(code = 200, message = "Группа получателей успешно сохранена")
    void updateRecipientGroup(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                              @PathParam("id") @ApiParam(value = "Идентификатор группы получателей") Integer id,
                              @ApiParam(value = "Группа получателей") RecipientGroup recipientGroup);

    @DELETE
    @Path("/{id}")
    @ApiOperation("Удаление группы получателей")
    @ApiResponse(code = 200, message = "Группа получателей успешно удалена")
    void deleteRecipientGroup(@PathParam("tenantCode") @ApiParam(value = "Код тенанта") String tenantCode,
                        @PathParam("id") @ApiParam(value = "Идентификатор группы получателей") Integer id);

}
