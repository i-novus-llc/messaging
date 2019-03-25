package ru.inovus.messaging.impl;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.Severity;

public class SeverityConverterForMessageSetting extends EnumConverter<String, Severity> {
    public SeverityConverterForMessageSetting() {
        super(String.class, Severity.class);
    }

    @Override
    public String to(Severity userObject) {
        return userObject.getValue();
    }
}
