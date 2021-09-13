package ru.inovus.messaging.api.model.enums;

/**
 * Статус доставки уведомления
 */
public enum SendStatus {
    SCHEDULED("Ожидает отправки"),
    SENT("Отправлено получателю"),
    FAILED("Ошибка отправки получателю");

    private String name;

    SendStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
