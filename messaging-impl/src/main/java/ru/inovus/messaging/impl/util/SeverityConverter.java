package ru.inovus.messaging.impl.util;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.enums.Severity;

public class SeverityConverter extends EnumConverter<String, Severity> {
    public SeverityConverter() {
        super(String.class, Severity.class);
    }

    @Override
    public String to(Severity severity) {
        return severity.getValue();
    }
}
