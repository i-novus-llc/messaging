package ru.inovus.messaging.api;

import java.io.Serializable;

public class UnreadMessagesInfo implements Serializable {
    private static final long serialVersionUID = 3983516912352834017L;

    private Integer count;

    public UnreadMessagesInfo() {
    }

    public UnreadMessagesInfo(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
