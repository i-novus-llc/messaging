package ru.inovus.messaging.impl.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import net.n2oapp.platform.i18n.Message;
import net.n2oapp.platform.i18n.UserException;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.jooq.impl.DSL.noCondition;
import static org.springframework.util.StringUtils.hasText;
import static ru.inovus.messaging.impl.jooq.Tables.ATTACHMENT;

/**
 * Сервис работы с вложениями
 */
public class AttachmentService implements AttachmentRest, MessageAttachment {

    @Autowired
    private DSLContext dsl;
    @Autowired
    private AmazonS3 s3client;
    @Autowired
    private DocumentUtils documentUtils;
    @Value("${novus.messaging.attachment.s3.bucket-name:messaging}")
    private String bucketName;
    @Value("${novus.messaging.attachment.file-count:5}")
    private Integer maxFileCount;

    private final RecordMapper<Record, AttachmentResponse> MAPPER = rec -> {
        AttachmentRecord record = rec.into(ATTACHMENT);
        AttachmentResponse response = new AttachmentResponse();
        response.setId(record.getId());
        response.setShortFileName(record.getFile().substring(documentUtils.getDateTimePrefixLength()));
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
        if (!StringUtils.hasText(fileName)) return null;
        documentUtils.checkFileExtension(fileName);
        fileName = documentUtils.getFileNameWithDateTimePrefix(fileName);

        InputStream is;
        Integer fileSize;

        try {
            is = attachment.getDataHandler().getInputStream();
            fileSize = documentUtils.checkFileSize(is);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }

        createBucketIfNotExist();
        upload(fileName, is);

        AttachmentResponse fileResponse = new AttachmentResponse();
        fileResponse.setFileName(fileName);
        fileResponse.setShortFileName(fileName.substring(documentUtils.getDateTimePrefixLength()));
        fileResponse.setFileSize(fileSize);
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
                                        .filename(fileName.substring(documentUtils.getDateTimePrefixLength()), StandardCharsets.UTF_8)
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
            validateAttachments(files);
            List<AttachmentRecord> attachments = files.stream()
                    .map(attachment -> new AttachmentRecord(UUID.randomUUID(), messageId, attachment.getFileName(), LocalDateTime.now()))
                    .collect(Collectors.toList());
            dsl.batchInsert(attachments).execute();
        }
    }

    private void validateAttachments(List<AttachmentResponse> files) {
        if (files.size() > maxFileCount)
            throw new UserException(new Message("messaging.exception.file.count", maxFileCount));
        validateDuplicates(files);
    }

    private void validateDuplicates(List<AttachmentResponse> files) {
        Set<AttachmentResponse> checkedAttachments = new HashSet<>();
        Set<AttachmentResponse> duplicates = new HashSet<>();

        for (AttachmentResponse attachment : files) {
            if (checkedAttachments.contains(attachment))
                duplicates.add(attachment);
            else checkedAttachments.add(attachment);
        }

        if (!CollectionUtils.isEmpty(duplicates))
            throw new UserException(new Message("messaging.exception.file.duplicate", duplicates.stream()
                    .map(AttachmentResponse::getShortFileName)
                    .collect(Collectors.joining(", "))));
    }

    private SelectConditionStep<Record> getFindByMessageIdQuery(UUID messageId) {
        return dsl
                .select(ATTACHMENT.fields())
                .from(ATTACHMENT)
                .where(nonNull(messageId)
                        ? ATTACHMENT.MESSAGE_ID.cast(UUID.class).eq(messageId)
                        : noCondition());
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