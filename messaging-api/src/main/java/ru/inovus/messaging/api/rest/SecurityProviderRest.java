package ru.inovus.messaging.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.api.model.Role;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.SecurityBaseCriteria;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Api(value = "Данные о пользователях", authorizations = @Authorization(value = "oauth2"))
@Path("/security_provider")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SecurityProviderRest {

    @GET
    @Path("/roles")
    @ApiOperation("Получение списка ролей")
    @ApiResponse(code = 200, message = "Список ролей")
    Page<Role> getRoles(@BeanParam SecurityBaseCriteria criteria);

    @GET
    @Path("/regions")
    @ApiOperation("Получение списка регионов")
    @ApiResponse(code = 200, message = "Список регионов")
    Page<Region> getRegions(@BeanParam SecurityBaseCriteria criteria);

    @GET
    @Path("/organizations")
    @ApiOperation("Получение списка медицинских организаций")
    @ApiResponse(code = 200, message = "Список медицинских организаций")
    Page<Organization> getMedOrganizations(@BeanParam SecurityBaseCriteria criteria);
}
