package ru.inovus.messaging.channel.api.queue.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnreadMessagesQO extends QueueObject {
    private Integer count;
}
