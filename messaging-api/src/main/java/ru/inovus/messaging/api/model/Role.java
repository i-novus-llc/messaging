package ru.inovus.messaging.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role {

    private Integer id;

    private String name;

    private String code;

    private String description;
}