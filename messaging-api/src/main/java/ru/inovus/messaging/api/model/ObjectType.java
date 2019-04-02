package ru.inovus.messaging.api.model;

import lombok.Getter;

@Getter
public enum ObjectType {
    LOAN_SELECTION_FILE("Файл выборки кредитных средств"),
    SURETY_CONTRACT("Договор поручительства");

    String name;

    ObjectType(String name) {
        this.name = name;
    }
}
