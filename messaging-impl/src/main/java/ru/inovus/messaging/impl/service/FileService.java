package ru.inovus.messaging.impl.service;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.springframework.stereotype.Service;
import ru.inovus.messaging.api.model.FileResponse;

import javax.ws.rs.core.Response;
import java.util.UUID;

@Service
public class FileService {

    public FileResponse upload(Attachment attachment) {
        FileResponse fileResponse = new FileResponse();
        fileResponse.setId("1");
        fileResponse.setFileName("test name");
        fileResponse.setURL("leningrad.ru");
        return fileResponse;
    }

    public Response download(UUID id) {
        return Response.accepted().build();
    }

    public Response delete(String fileName) {
        return Response.accepted().build();
    }
}
