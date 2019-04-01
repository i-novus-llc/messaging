package ru.inovus.messaging.api.model;

import lombok.Getter;

@Getter
public enum Component {
    PAYMENT("PAYMENT", 1);

    String name;
    Integer id;

    Component(String name, Integer id) {
        this.id = id;
    }
}
