package ru.inovus.messaging.api.model;

public enum Severity {
    INFO("10", "Информация"),
    WARNING("20", "Предупреждение"),
    ERROR("30", "Ошибка"),
    SEVERE("40", "Важный");

    private String value;
    private String name;

    Severity(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
