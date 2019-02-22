package ru.inovus.messaging.api;

public enum FormationType {
    AUTO, HAND;

    public String getName() {
        switch (this) {
            case AUTO: return "Автоматическое";
            case HAND: return "Ручное";
            default: return null;
        }
    }
}
