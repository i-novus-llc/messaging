package ru.inovus.messaging.api.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Тип статуса уведомления
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MessageStatusType {
    SCHEDULED("Ожидает отправки"),
    SENT("Отправлено получателю"),
    FAILED("Ошибка отправки получателю"),
    READ("Прочитано");

    private String description;

    MessageStatusType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name();
    }

    /**
     * Возврат типа предыдущего состояния уведомления.
     * Нужен для проверки возможности перехода от одного статуса к другому.
     * Например, нельзя перейти от статуса FAILED к READ и т.д.
     *
     * @return Тип предыдущего состояния уведомления
     */
    @JsonIgnore
    public MessageStatusType getPrevStatus() {
        return switch (this) {
            case SCHEDULED, SENT, FAILED -> SCHEDULED;
            case READ -> SENT;
        };
    }
}
