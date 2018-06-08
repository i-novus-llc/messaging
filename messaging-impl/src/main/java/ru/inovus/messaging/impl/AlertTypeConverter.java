package ru.inovus.messaging.impl;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.AlertType;

public class AlertTypeConverter extends EnumConverter<String, AlertType> {
    public AlertTypeConverter() {
        super(String.class, AlertType.class);
    }
}
