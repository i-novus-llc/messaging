package ru.inovus.messaging.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class User {

    private Integer id;

    private String username;

    private String fio;

    private String email;

    private String surname;

    private String name;

    private String patronymic;

    private Boolean isActive;

    private List<Role> roles;
}
