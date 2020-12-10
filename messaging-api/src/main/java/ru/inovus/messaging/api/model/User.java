package ru.inovus.messaging.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class User {
    private String username;

    private String fio;

    private String email;

    private String surname;

    private String name;

    private String patronymic;

    private List<Role> roles;
}
