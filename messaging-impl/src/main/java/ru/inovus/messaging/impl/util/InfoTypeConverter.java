package ru.inovus.messaging.impl.util;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.InfoType;

public class InfoTypeConverter extends EnumConverter<String, InfoType> {
    public InfoTypeConverter() {
        super(String.class, InfoType.class);
    }
}
