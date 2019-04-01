package ru.inovus.messaging.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.n2oapp.platform.jaxrs.MapperConfigurer;
import ru.inovus.messaging.server.rest.LocalDateTimeDeserializer;
import ru.inovus.messaging.server.rest.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class DateMapperConfigurer implements MapperConfigurer {
    @Override
    public void configure(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new LocalDateTimeSerializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        mapper.registerModule(module);
    }
}
