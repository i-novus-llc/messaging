package ru.inovus.messaging.impl.service;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.springframework.stereotype.Service;
import ru.inovus.messaging.api.model.FileResponse;

import javax.ws.rs.core.Response;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final s3Service s3Service;
    //todo repository

    public FileResponse upload(Attachment attachment) {
        //todo в бд имя файла
        //todo id в респонс засунуть
        return s3Service.upload(attachment);
    }

    public Response download(UUID id) {
        //todo найти по айди файлнейм
        return s3Service.download("xxx");
    }

    public Response delete(String fileName) {
        //todo удалить из бд запись
        return s3Service.delete(fileName);
    }

}
