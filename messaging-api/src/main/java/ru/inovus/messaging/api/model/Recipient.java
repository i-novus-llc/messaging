package ru.inovus.messaging.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Recipient implements Serializable {

    private static final long serialVersionUID = 4960064989162334509L;

    private Long id;
    private String recipient;
    private LocalDateTime readAt;
    private UUID messageId;
    private String name;
    private String email;

    public Recipient() {
    }

    public Recipient(String recipient) {
        this.recipient = recipient;
    }

}