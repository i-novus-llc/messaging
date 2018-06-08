package ru.inovus.messaging.impl.rest;

import ru.inovus.messaging.api.Message;

import java.io.Serializable;
import java.util.List;

public class MessagingResponse implements Serializable {
    private static final long serialVersionUID = -5507323311432463659L;

    private Integer count;
    private List<Message> collection;

    public MessagingResponse() {
    }

    public MessagingResponse(Integer count, List<Message> collection) {
        this.count = count;
        this.collection = collection;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Message> getCollection() {
        return collection;
    }

    public void setCollection(List<Message> collection) {
        this.collection = collection;
    }
}
