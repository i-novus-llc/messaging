package ru.inovus.messaging.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(using = InfoTypeSerializer.class)
@JsonDeserialize(using = InfoTypeDeserializer.class)
public enum InfoType implements Serializable {
    NOTICE("Центр уведомлений"),
    EMAIL("Электронная почта");

    private String name;

    InfoType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
