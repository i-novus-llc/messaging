package ru.inovus.messaging.impl.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import net.n2oapp.platform.i18n.Message;
import net.n2oapp.platform.i18n.UserException;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ContentDisposition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.inovus.messaging.api.MessageAttachment;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.AttachmentResponse;
import ru.inovus.messaging.api.rest.AttachmentRest;
import ru.inovus.messaging.impl.jooq.tables.records.AttachmentRecord;
import ru.inovus.messaging.impl.util.DocumentUtils;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;
import static ru.inovus.messaging.impl.jooq.Tables.ATTACHMENT;
import static ru.inovus.messaging.impl.util.DocumentUtils.DATE_TIME_PREFIX_LENGTH;

/**
 * Сервис работы с вложениями
 */
@RequiredArgsConstructor
public class AttachmentService implements AttachmentRest, MessageAttachment {

    private final DSLContext dsl;
    private final AmazonS3 s3client;
    private final DocumentUtils documentUtils;

    @Value("${novus.messaging.attachment.s3.bucket-name}")
    private String bucketName;
    @Value("${novus.messaging.attachment.file-count}")
    private Integer maxFileCount;

    private final RecordMapper<Record, AttachmentResponse> MAPPER = rec -> {
        AttachmentRecord record = rec.into(ATTACHMENT);
        AttachmentResponse response = new AttachmentResponse();
        response.setId(record.getId());
        response.setShortFileName(record.getFile().substring(DATE_TIME_PREFIX_LENGTH));
        response.setFileName(record.getFile());
        return response;
    };

    public Page<AttachmentResponse> findAll(RecipientCriteria criteria) {
        int count = dsl.fetchCount(getFindByMessageIdQuery(criteria.getMessageId()));

        List<AttachmentResponse> attachments = getFindByMessageIdQuery(criteria.getMessageId())
                .limit(criteria.getPageSize())
                .offset(criteria.getOffset())
                .fetch(MAPPER);
        return new PageImpl<>(attachments, criteria, count);
    }

    public List<AttachmentResponse> findAll(UUID messageId) {
        return getFindByMessageIdQuery(messageId).fetch(MAPPER);
    }

    public AttachmentResponse upload(Attachment attachment) {
        String fileName = documentUtils.getFileName(attachment);
        if (StringUtils.isEmpty(fileName)) return null;
        documentUtils.checkFileExtension(fileName);
        fileName = documentUtils.getFileNameWithDateTime(fileName);

        InputStream is;

        try {
            is = attachment.getDataHandler().getInputStream();
            documentUtils.checkFileSize(is);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }

        createBucketIfNotExist();
        upload(fileName, is);

        AttachmentResponse fileResponse = new AttachmentResponse();
        fileResponse.setFileName(fileName);
        fileResponse.setShortFileName(fileName.substring(DATE_TIME_PREFIX_LENGTH));
        return fileResponse;
    }

    public Response download(UUID id) {
        Record record = dsl
                .select(ATTACHMENT.fields())
                .from(ATTACHMENT)
                .where(ATTACHMENT.ID.cast(UUID.class).eq(id))
                .fetchOne();

        if (nonNull(record)) {
            AttachmentRecord attachment = record.into(ATTACHMENT);
            String fileName = attachment.getFile();
            if (hasText(fileName)) {
                InputStream is = download(fileName);
                return Response
                        .ok(is, MediaType.APPLICATION_OCTET_STREAM)
                        .header(
                                HttpHeaders.CONTENT_DISPOSITION,
                                ContentDisposition.builder("attachment")
                                        .filename(fileName.substring(DATE_TIME_PREFIX_LENGTH), StandardCharsets.UTF_8)
                                        .build()
                                        .toString())
                        .build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public InputStream download(String fileName) {
        try {
            S3Object s3object = s3client.getObject(bucketName, fileName);
            return s3object.getObjectContent();
        } catch (Exception e) {
            throw new UserException(new Message("messaging.exception.file.download"), e);
        }
    }

    public Response delete(String fileName) {
        if (hasText(fileName)) {
            try {
                s3client.deleteObject(bucketName, fileName);
            } catch (Exception e) {
                throw new UserException(new Message("messaging.exception.file.remove"), e);
            }
        }
        return Response.ok().build();
    }

    public void create(List<AttachmentResponse> files, UUID messageId) {
        if (!CollectionUtils.isEmpty(files) && nonNull(messageId)) {
            if (files.size() > maxFileCount)
                throw new UserException(new Message("messaging.exception.file.count", maxFileCount));

            List<AttachmentRecord> attachments = files.stream()
                    .map(attachment -> new AttachmentRecord(UUID.randomUUID(), messageId, attachment.getFileName(), LocalDateTime.now()))
                    .collect(Collectors.toList());
            dsl.batchInsert(attachments).execute();
        }
    }

    private SelectConditionStep<Record> getFindByMessageIdQuery(UUID messageId) {
        return dsl
                .select(ATTACHMENT.fields())
                .from(ATTACHMENT)
                .where(ATTACHMENT.MESSAGE_ID.cast(UUID.class).eq(messageId));
    }

    private void upload(String fileName, InputStream file) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setCacheControl("max-age=2592000, must-revalidate");
            metadata.setContentLength(file.available());
            s3client.putObject(bucketName, fileName, file, metadata);
        } catch (Exception e) {
            throw new UserException("messaging.exception.file.upload", e);
        }
    }

    private void createBucketIfNotExist() {
        if (!s3client.doesBucketExistV2(bucketName))
            s3client.createBucket(bucketName);
    }
}