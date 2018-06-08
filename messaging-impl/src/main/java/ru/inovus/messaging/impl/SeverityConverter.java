package ru.inovus.messaging.impl;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.Severity;

public class SeverityConverter extends EnumConverter<String, Severity> {
    public SeverityConverter() {
        super(String.class, Severity.class);
    }
}
