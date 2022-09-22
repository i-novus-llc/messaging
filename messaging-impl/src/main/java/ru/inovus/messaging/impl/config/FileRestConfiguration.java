package ru.inovus.messaging.impl.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.AwsHostNameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.inovus.messaging.api.rest.FileRest;
import ru.inovus.messaging.impl.rest.FileRestImpl;

@Configuration
@ConditionalOnProperty(value = "novus.messaging.attachment.enabled", havingValue = "true")
public class FileRestConfiguration {

    @Bean
    FileRest fileRest() {
        return new FileRestImpl();
    }

    @Bean
    AmazonS3 s3client(@Value("${novus.messaging.attachment.s3.access-key}") String accessKey,
                      @Value("${novus.messaging.attachment.s3.secret-key}") String secretKey,
                      @Value("${novus.messaging.attachment.s3.endpoint") String endpoint) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(endpoint, AwsHostNameUtils.parseRegion(endpoint, AmazonS3Client.S3_SERVICE_NAME));
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }
}