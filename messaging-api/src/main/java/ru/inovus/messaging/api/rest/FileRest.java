package ru.inovus.messaging.api.rest;

import io.swagger.annotations.*;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import ru.inovus.messaging.api.model.FileResponse;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Api(value = "Операции над прикрепленными файлами", authorizations = @Authorization(value = "oauth2"))
@Path("/files")
public interface FileRest {
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation("Загрузка файла")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "Файл", required = true, dataType = "java.io.File", paramType = "form")
    })
    FileResponse upload(@NotNull @Multipart(value = "file") Attachment attachment);

    @GET
    @Path("/{id}/download")
    @ApiOperation("Скачивание файла")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    Response download(@ApiParam(value = "Идентификатор файла") @PathParam("id") UUID id); //todo fileName?

    @DELETE
    @Path("/delete/{fileName}")
    @ApiOperation("Удаление обязательного документа из MinIO")
    Response delete(@PathParam("fileName") String fileName);
}
