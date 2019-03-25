package ru.inovus.messaging.impl;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.Severity;

public class SeverityConverterForMessage extends EnumConverter<String, Severity> {
    public SeverityConverterForMessage() {
        super(String.class, Severity.class);
    }
}
