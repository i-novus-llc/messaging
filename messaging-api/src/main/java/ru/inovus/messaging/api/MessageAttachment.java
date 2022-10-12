package ru.inovus.messaging.api;

import ru.inovus.messaging.api.model.AttachmentResponse;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * Операции над вложениями
 */
public interface MessageAttachment {
    List<AttachmentResponse> findAll(UUID messageId);

    void create(List<AttachmentResponse> files, UUID messageId);

    InputStream download(String fileName);
}



