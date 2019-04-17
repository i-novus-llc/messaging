package ru.inovus.messaging.api.model;

public enum RecipientType {
    USER("Пользователи"),
    ALL("Все");

    private String name;

    RecipientType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
