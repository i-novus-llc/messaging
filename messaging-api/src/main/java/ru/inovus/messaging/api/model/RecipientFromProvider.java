package ru.inovus.messaging.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipientFromProvider {
    private String username;
    private String email;
    private String fio;
    private String surname;
    private String name;
    private String patronymic;
}

