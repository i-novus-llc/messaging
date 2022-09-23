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

@Api(value = "Операции над прикрепленными файлами", authorizations = @Authorization(value = "oauth2"))
@Path("/attachments")
public interface AttachmentRest {

    @GET
    @Path("/")
    @ApiOperation("Получение списка прикрепленных файлов")
    @ApiResponse(code = 200, message = "Список прикрепленных файлов")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Page<AttachmentResponse> findAll(@BeanParam RecipientCriteria criteria);

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation("Загрузка файла")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "Файл", required = true, dataType = "java.io.File", paramType = "form")
    })
    AttachmentResponse upload(@NotNull @Multipart(value = "file") Attachment attachment);

    @GET
    @Path("/{id}")
    @ApiOperation("Скачивание файла")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    Response download(@ApiParam(value = "Идентификатор файла") @PathParam("id") UUID id);

    @DELETE
    @Path("/{fileName}")
    @ApiOperation("Удаление файла")
    Response delete(@PathParam("fileName") String fileName);
}
