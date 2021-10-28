package ru.inovus.messaging.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderRecipient {

    @ApiModelProperty("Имя получателя")
    private String name;

    @ApiModelProperty("Фамилия получателя")
    private String surname;

    @ApiModelProperty("Отчество получателя")
    private String patronymic;

    @ApiModelProperty("Имя пользователя получателя")
    private String username;

    @ApiModelProperty("Email получателя")
    private String email;

    @ApiModelProperty("ФИО получателя")
    private String fio;
}

