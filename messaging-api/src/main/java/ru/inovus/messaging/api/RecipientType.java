package ru.inovus.messaging.api;

public enum RecipientType {
    USER("Пользователь"),
    ALL("Все");

    private String name;

    RecipientType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
