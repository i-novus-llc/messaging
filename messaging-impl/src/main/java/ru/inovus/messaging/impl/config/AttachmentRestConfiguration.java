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

@Configuration
@ConditionalOnProperty(value = "novus.messaging.attachment.enabled", havingValue = "true")
public class AttachmentRestConfiguration {

    @Bean
    AttachmentService attachmentService(DSLContext dsl, AmazonS3 s3client, DocumentUtils documentUtils) {
        return new AttachmentService(dsl, s3client, documentUtils);
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
    DocumentUtils documentUtils() {
        return new DocumentUtils();
    }
}