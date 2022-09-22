package ru.inovus.messaging.impl.config;

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
}