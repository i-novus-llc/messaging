package ru.inovus.messaging.server;

import lombok.Getter;
import lombok.Setter;

/**
 * Пример класса сообщения
 */

@Getter
@Setter
public class ChatMessage {

    private String username;
    private String message;

    @Override
    public String toString() {
        return "ChatMessage [user=" + username + ", message=" + message + "]";
    }
}


