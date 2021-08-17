package ru.inovus.messaging.impl.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import net.n2oapp.platform.jaxrs.MapperConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.inovus.messaging.impl.rest.LocalDateTimeDeserializer;
import ru.inovus.messaging.impl.rest.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Configuration
public class DateMapperConfiguration {
    @Bean
    public MapperConfigurer dateMapperConfigurer() {
        return mapper -> {
            SimpleModule module = new SimpleModule();
            module.addSerializer(new LocalDateTimeSerializer());
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
            mapper.registerModule(module);
        };
    }
}
