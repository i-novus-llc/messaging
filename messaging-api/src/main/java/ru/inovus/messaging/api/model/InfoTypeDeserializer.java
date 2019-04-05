package ru.inovus.messaging.api.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class InfoTypeDeserializer extends StdDeserializer<InfoType> {

    protected InfoTypeDeserializer() {
        super(InfoType.class);
    }

    @Override
    public InfoType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node.get("id") == null) {
            return null;
        }
        return InfoType.valueOf(node.get("id").asText());
    }
}
