package ru.inovus.messaging.api.model;

public enum YesNo {
    YES(true),NO(false);

    private Boolean value;

    YesNo(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }
}
