package ru.inovus.messaging.api.model.enums;

/**
 * Тип статуса уведомления
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

    /**
     * Возврат типа предыдущего состояния уведомления.
     * Нужен для проверки возможности перехода от одного статуса к другому.
     * Например, нельзя перейти от статуса FAILED к READ и т.д.
     * @return Тип предыдущего состояния уведомления
     */
    public MessageStatusType getPrevStatus() {
        return switch (this) {
            case SCHEDULED, SENT, FAILED -> SCHEDULED;
            case READ -> SENT;
        };
    }
}
