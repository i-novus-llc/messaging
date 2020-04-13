package ru.inovus.messaging.api.model;

public enum AlertType {
    BLOCKER,
    POPUP,
    HIDDEN;

    public String getName() {
        switch (this) {
            case BLOCKER: return "Блокирующее сообщение";
            case POPUP: return "Всплывающее сообщение";
            case HIDDEN: return "Лента сообщений";
            default: return null;
        }
    }
}
