package ru.inovus.messaging.api;

public enum InfoType {
    ALL, NOTICE, EMAIL;

    public String getName() {
        switch (this) {
            case ALL: return "Все";
            case NOTICE: return "Центр уведомлений";
            case EMAIL: return "Электронная почта";
            default: return null;
        }
    }
}
