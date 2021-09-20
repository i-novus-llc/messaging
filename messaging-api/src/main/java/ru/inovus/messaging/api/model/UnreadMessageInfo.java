package ru.inovus.messaging.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UnreadMessageInfo {
    private Integer count;
    private String systemId;
    private String recipientSendChannelId;
}
