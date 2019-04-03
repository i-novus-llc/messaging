package ru.inovus.messaging.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.security.admin.api.model.User;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.UserCriteria;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Api("Пользователи")
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserRest {
    @GET
    @ApiOperation("Получение списка пользователей")
    @ApiResponse(code = 200, message = "Список пользователей")
    Page<User> getUsers(@BeanParam UserCriteria criteria);
}
