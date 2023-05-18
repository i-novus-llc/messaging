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

    private String name;

    MessageStatusType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
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
        return this == READ ? SENT : SCHEDULED;
    }
}
