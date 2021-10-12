package ru.inovus.messaging.impl.util;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.enums.AlertType;

public class AlertTypeConverter extends EnumConverter<String, AlertType> {
    public AlertTypeConverter() {
        super(String.class, AlertType.class);
    }
}
