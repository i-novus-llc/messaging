package ru.inovus.messaging.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("Количество непрочитанных уведомлений пользователем")
    private Integer count;

    @JsonIgnore
    private String tenantCode;

    @JsonIgnore
    private String username;

    public FeedCount(String tenantCode, String username, Integer count) {
        this.tenantCode = tenantCode;
        this.username = username;
        this.count = count;
    }
}
