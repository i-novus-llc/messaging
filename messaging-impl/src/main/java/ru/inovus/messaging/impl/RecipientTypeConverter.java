package ru.inovus.messaging.impl;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.RecipientType;

public class RecipientTypeConverter extends EnumConverter<String, RecipientType> {
    public RecipientTypeConverter() {
        super(String.class, RecipientType.class);
    }
}
