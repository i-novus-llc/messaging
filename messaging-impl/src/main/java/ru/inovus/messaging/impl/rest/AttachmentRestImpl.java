package ru.inovus.messaging.impl.rest;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.AttachmentResponse;
import ru.inovus.messaging.api.rest.AttachmentRest;
import ru.inovus.messaging.impl.service.AttachmentService;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
public class AttachmentRestImpl implements AttachmentRest {

    private final AttachmentService fileService;

    @Override
    public Page<AttachmentResponse> findAll(RecipientCriteria criteria) {
        return fileService.findAll(criteria);
    }

    @Override
    public AttachmentResponse upload(Attachment attachment) {
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

    @Override
    public InputStream downloadIS(String fileName) {
        return fileService.downloadIS(fileName);
    }
}