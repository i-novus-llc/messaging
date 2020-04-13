package ru.inovus.messaging.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Component implements Serializable {
    Integer id;
    String name;

    public Component() {
    }

    public Component(Integer id, String name) {
        this.name = name;
        this.id = id;
    }
}
