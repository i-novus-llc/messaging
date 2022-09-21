package ru.inovus.messaging.impl.rest;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import ru.inovus.messaging.api.model.FileResponse;
import ru.inovus.messaging.api.rest.FileRest;
import ru.inovus.messaging.impl.service.FileService;

import javax.ws.rs.core.Response;
import java.util.UUID;

public class FileRestImpl implements FileRest {

    @Autowired
    public FileService fileService;

    @Override
    public FileResponse upload(Attachment attachment) {
        return fileService.upload(attachment);
    }

    @Override
    public Response download(UUID id) {
        return fileService.download(id);
    }

    @Override
    public Response delete(String fileName) {
        return fileService.delete(fileName);
    }
}