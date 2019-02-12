package ru.inovus.messaging.server.auth;

import org.springframework.security.core.Authentication;

public interface WebSocketAuthenticator {

    Authentication authenticate(String authToken);
}
