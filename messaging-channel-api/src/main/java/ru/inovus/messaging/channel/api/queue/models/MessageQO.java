package ru.inovus.messaging.channel.api.queue.models;

import lombok.Getter;
import lombok.Setter;
import ru.inovus.messaging.api.model.enums.Severity;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MessageQO extends QueueObject {
    private UUID id;
    private String caption;
    private String text;
    private Severity severity;
    private List<RecipientQO> recipients;

    @Getter
    @Setter
    public static class RecipientQO {
        private String name;
        private String email;
    }
}
