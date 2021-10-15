package ru.inovus.messaging.api.model;

import lombok.Getter;
import lombok.Setter;
import ru.inovus.messaging.api.model.enums.MessageStatusType;

import java.io.Serializable;

/**
 * Статус уведомления.
 * Предназначен для передачи информации в очередь статусов.
 */
@Getter
@Setter
public class MessageStatus implements Serializable {
    private String tenantCode;
    private String username;
    private String messageId;
    private MessageStatusType status;
    private String errorMessage;
}