package ru.inovus.messaging.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Component implements Serializable {

    private Integer id;
    private String name;

    public Component() {
    }

    public Component(Integer id) {
        this.id = id;
    }

    public Component(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
