package ru.inovus.messaging.api.model.enums;

/**
 * Статус уведомления
 */
public enum MessageStatusType {
    FAILED("Ошибка отправки получателю"),
    SCHEDULED("Ожидает отправки"),
    SENT("Отправлено получателю"),
    READ("Прочитано");

    private String name;

    MessageStatusType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
