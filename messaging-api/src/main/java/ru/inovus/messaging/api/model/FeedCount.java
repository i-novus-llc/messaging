package ru.inovus.messaging.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Количество непрочитанных уведомлений пользователем.
 * Предназначен для передачи информации в очередь счетчиков непрочитанных уведомлений.
 */
@Getter
@Setter
public class FeedCount implements Serializable {
    private String systemId;
    private String username;
    private Integer count;

    public FeedCount(String systemId, String username, Integer count) {
        this.systemId = systemId;
        this.username = username;
        this.count = count;
    }
}
