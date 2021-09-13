package ru.inovus.messaging.channel.api.queue.models;

import lombok.Getter;
import lombok.Setter;
import ru.inovus.messaging.api.model.enums.SendStatus;

import java.util.UUID;

@Getter
@Setter
public class MessageStatusQO extends QueueObject {
    private UUID messageId;
    private Integer recipientId;
    private SendStatus status;
    private String errorMessage;
}
