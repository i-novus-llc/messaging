package ru.inovus.messaging.api;

public enum Severity {
    INFO,
    WARNING,
    ERROR,
    SEVERE;

    public String getName() {
        switch (this) {
            case SEVERE: return "Важный";
            case ERROR: return "Ошибка";
            case WARNING: return "Предупреждение";
            case INFO: return "Информация";
            default: return null;
        }
    }
}
