package ru.inovus.messaging.impl;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.Severity;

public class SeverityConverter extends EnumConverter<String, Severity> {
    public SeverityConverter() {
        super(String.class, Severity.class);
    }

    @Override
    public String to(Severity severity) {
        return severity.getValue();
    }
}
