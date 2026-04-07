package ru.inovus.messaging.impl.util;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.enums.MessageType;

public class MessageTypeConverter extends EnumConverter<String, MessageType> {
    public MessageTypeConverter() {
        super(String.class, MessageType.class);
    }
}
