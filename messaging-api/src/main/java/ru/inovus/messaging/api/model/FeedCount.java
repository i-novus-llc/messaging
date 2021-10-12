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
    private String tenantCode;
    private String username;
    private Integer count;

    public FeedCount(String tenantCode, String username, Integer count) {
        this.tenantCode = tenantCode;
        this.username = username;
        this.count = count;
    }
}
