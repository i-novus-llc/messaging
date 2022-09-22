package ru.inovus.messaging.impl.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import net.n2oapp.platform.i18n.Message;
import net.n2oapp.platform.i18n.UserException;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import ru.inovus.messaging.api.model.FileResponse;
import ru.inovus.messaging.impl.util.DocumentUtils;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.util.StringUtils.hasText;
import static ru.inovus.messaging.impl.util.DocumentUtils.DATE_TIME_PREFIX_LENGTH;

@Service
@RequiredArgsConstructor
public class s3Service {

    private final AmazonS3 s3client;
    private final DocumentUtils documentUtils;
    @Value("${novus.messaging.attachment.s3.bucket-name")
    private final String bucketName;

    public FileResponse upload(Attachment attachment) {
        String fileName = documentUtils.getFileName(attachment);
        documentUtils.checkFileExtension(fileName);
        fileName = documentUtils.getFileNameWithDateTime(fileName);

        InputStream is;

        try {
            is = attachment.getDataHandler().getInputStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }

        upload(fileName, is);

        FileResponse fileResponse = new FileResponse();
        fileResponse.setFileName(fileName.substring(DATE_TIME_PREFIX_LENGTH));
        return fileResponse;
    }

    public Response download(String fileName) {
        if (!hasText(fileName))
            return null;

        InputStream is = downloadIS(fileName);
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


    public Response delete(String fileName) {
        try {
            s3client.deleteObject(bucketName, fileName);
            return Response.accepted().build();
        } catch (Exception e) {
            throw new UserException(new Message("messaging.exception.file.remove"), e);
        }
    }

    private void upload(String fileName, InputStream file) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setCacheControl("max-age=2592000, must-revalidate");
            s3client.putObject(bucketName, fileName, file, metadata);
        } catch (Exception e) {
            throw new UserException("messaging.exception.file.upload", e);
        }
    }

    private InputStream downloadIS(String fileName) {
        try {
            S3Object s3object = s3client.getObject(bucketName, fileName);
            return s3object.getObjectContent();
        } catch (Exception e) {
            throw new UserException(new Message("messaging.exception.file.download"), e);
        }
    }

    @PostConstruct
    private void createBucketIfNotExist() {
        if (!s3client.doesBucketExistV2(bucketName))
            s3client.createBucket(bucketName);
    }
}