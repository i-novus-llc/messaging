package ru.inovus.messaging.impl.util;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.enums.Severity;

import static java.util.Objects.isNull;

public class SeverityConverter extends EnumConverter<String, Severity> {
    public SeverityConverter() {
        super(String.class, Severity.class);
    }

    @Override
    public String to(Severity severity) {
        if (isNull(severity)) return null;
        return severity.getValue();
    }
}
