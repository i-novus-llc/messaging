package ru.inovus.messaging.impl;

import org.jooq.impl.EnumConverter;
import ru.inovus.messaging.api.model.ObjectType;

public class ObjectTypeConverter extends EnumConverter<String, ObjectType> {
    public ObjectTypeConverter() {
        super(String.class, ObjectType.class);
    }
}
