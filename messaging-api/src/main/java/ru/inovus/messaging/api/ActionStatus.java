package ru.inovus.messaging.api;

import java.io.Serializable;

public class ActionStatus implements Serializable {
    private static final long serialVersionUID = 1681437091657789846L;

    private String result;

    public ActionStatus() {
    }

    public ActionStatus(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ActionStatus{" +
                "result='" + result + '\'' +
                '}';
    }
}
