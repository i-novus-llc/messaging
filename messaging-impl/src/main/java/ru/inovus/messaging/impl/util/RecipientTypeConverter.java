package ru.inovus.messaging.impl.util;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.RecipientType;

public class RecipientTypeConverter extends EnumConverter<String, RecipientType> {
    public RecipientTypeConverter() {
        super(String.class, RecipientType.class);
    }
}
