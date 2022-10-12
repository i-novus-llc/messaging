package ru.inovus.messaging.api.rest;

import io.swagger.annotations.*;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.AttachmentResponse;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Api(value = "Операции над вложениями", authorizations = @Authorization(value = "oauth2"))
@Path("/attachments")
public interface AttachmentRest {

    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Получение списка вложений")
    Page<AttachmentResponse> findAll(@BeanParam RecipientCriteria criteria);

    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation("Загрузка вложения")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "Файл", required = true, dataType = "java.io.File", paramType = "form")
    })
    AttachmentResponse upload(@NotNull @Multipart(value = "file") Attachment attachment);

    @GET
    @Path("/{id}")
    @ApiOperation("Скачивание вложения")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    Response download(@ApiParam(value = "Идентификатор файла") @PathParam("id") UUID id);

    @DELETE
    @Path("/{fileName}")
    @ApiOperation("Удаление вложения")
    Response delete(@PathParam("fileName") String fileName);
}
