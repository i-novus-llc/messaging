package ru.inovus.messaging.impl.util;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.enums.MessageStatus;

public class MessageStatusConverter extends EnumConverter<String, MessageStatus> {
    public MessageStatusConverter() {
        super(String.class, MessageStatus.class);
    }
}
