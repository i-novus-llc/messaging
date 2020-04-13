package ru.inovus.messaging.api.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class InfoTypeSerializer extends StdSerializer<InfoType> {

    protected InfoTypeSerializer() {
        super(InfoType.class);
    }

    @Override
    public void serialize(InfoType infoType, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        generator.writeFieldName("id");
        generator.writeString(infoType.name());
        generator.writeFieldName("name");
        generator.writeString(infoType.getName());
        generator.writeEndObject();
    }
}
