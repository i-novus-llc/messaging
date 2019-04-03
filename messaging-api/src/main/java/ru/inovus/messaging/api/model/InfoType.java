package ru.inovus.messaging.api.model;

public enum InfoType {
    NOTICE, EMAIL;

    public String getName() {
        switch (this) {
            case NOTICE:
                return "Центр уведомлений";
            case EMAIL:
                return "Электронная почта";
            default:
                return null;
        }
    }
}
