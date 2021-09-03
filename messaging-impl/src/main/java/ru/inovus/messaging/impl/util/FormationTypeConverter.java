package ru.inovus.messaging.impl.util;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.enums.FormationType;

public class FormationTypeConverter extends EnumConverter<String, FormationType> {
    public FormationTypeConverter() {
        super(String.class, FormationType.class);
    }
}
