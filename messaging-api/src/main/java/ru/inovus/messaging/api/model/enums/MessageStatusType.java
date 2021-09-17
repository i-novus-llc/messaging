package ru.inovus.messaging.api.model.enums;

/**
 * Статус уведомления
 */
public enum MessageStatusType {
    SCHEDULED("Ожидает отправки"),
    SENT("Отправлено получателю"),
    FAILED("Ошибка отправки получателю"),
    READ("Прочитано");

    private String name;

    MessageStatusType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
