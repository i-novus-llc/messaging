package ru.inovus.messaging.server.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import net.n2oapp.platform.jaxrs.RestCriteria;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.impl.jooq.tables.records.ComponentRecord;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static ru.inovus.messaging.impl.jooq.Tables.COMPONENT;

@Controller
@Path("/components")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ComponentRest {

    @Autowired
    private DSLContext dsl;

    @GET
    @ApiOperation("Получение страницы компонентов системы по названию")
    @ApiResponse(code = 200, message = "Страница компонентов системы")
    public Page<ComponentRecord> getComponents(@QueryParam("name") String name) {
        List<ComponentRecord> list = dsl
                .selectFrom(COMPONENT)
                .where(COMPONENT.NAME.containsIgnoreCase(name))
                .fetch();
        return new PageImpl<>(list, new RestCriteria(), list.size());
    }

}
