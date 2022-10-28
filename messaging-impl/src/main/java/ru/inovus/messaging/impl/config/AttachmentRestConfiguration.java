package ru.inovus.messaging.impl.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.AwsHostNameUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.inovus.messaging.impl.service.AttachmentService;
import ru.inovus.messaging.impl.util.DocumentUtils;

import java.util.List;

@Configuration
@ConditionalOnProperty(value = "novus.messaging.attachment.enabled", havingValue = "true")
public class AttachmentRestConfiguration {

    @Bean
    AttachmentService attachmentService(DSLContext dsl, AmazonS3 s3client, DocumentUtils documentUtils,
                                        @Value("${novus.messaging.attachment.s3.bucket-name}") String bucketName,
                                        @Value("${novus.messaging.attachment.file-count}") Integer maxFileCount) {
        return new AttachmentService(dsl, s3client, documentUtils, bucketName, maxFileCount);
    }

    @Bean
    AmazonS3 s3client(@Value("${novus.messaging.attachment.s3.access-key}") String accessKey,
                      @Value("${novus.messaging.attachment.s3.secret-key}") String secretKey,
                      @Value("${novus.messaging.attachment.s3.endpoint}") String endpoint) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(endpoint, AwsHostNameUtils.parseRegion(endpoint, AmazonS3Client.S3_SERVICE_NAME));
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

    @Bean
    DocumentUtils documentUtils(@Value("${novus.messaging.attachment.file-type}") List<String> fileExtensionList,
                                @Value("${novus.messaging.attachment.file-size}") Integer maxFileSize,
                                @Value("${novus.messaging.attachment.file-prefix-format}") String dateTimeFormat) {
        return new DocumentUtils(fileExtensionList, maxFileSize, dateTimeFormat);
    }
}