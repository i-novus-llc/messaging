package ru.inovus.messaging.channel.api.queue.model;

import lombok.Getter;
import lombok.Setter;
import ru.inovus.messaging.api.model.enums.MessageStatus;

import java.io.Serializable;

/**
 * Статус уведомления.
 * Предназначен для передачи информации в очередь статусов.
 */
@Getter
@Setter
public class QueueMessageStatus implements Serializable {
    private String id;
    private MessageStatus status;
    private String sendErrorMessage;
}
