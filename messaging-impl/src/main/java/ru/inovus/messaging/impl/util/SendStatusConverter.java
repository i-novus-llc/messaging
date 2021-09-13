package ru.inovus.messaging.impl.util;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.enums.SendStatus;

public class SendStatusConverter extends EnumConverter<String, SendStatus> {
    public SendStatusConverter() {
        super(String.class, SendStatus.class);
    }
}
