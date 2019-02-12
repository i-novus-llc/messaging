package ru.inovus.messaging.server.auth;

import com.sun.security.auth.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class NoAuthAuthenticator implements WebSocketAuthenticator {
    @Override
    public Authentication authenticate(String authToken) {
        return new UsernamePasswordAuthenticationToken(new UserPrincipal(authToken), null);
    }
}
