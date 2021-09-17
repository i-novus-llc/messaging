package ru.inovus.messaging.impl.util;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.enums.MessageStatusType;

public class MessageStatusTypeConverter extends EnumConverter<String, MessageStatusType> {
    public MessageStatusTypeConverter() {
        super(String.class, MessageStatusType.class);
    }
}
