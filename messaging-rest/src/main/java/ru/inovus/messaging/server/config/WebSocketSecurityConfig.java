package ru.inovus.messaging.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import ru.inovus.messaging.server.auth.NoAuthAuthenticator;
import ru.inovus.messaging.server.auth.OAuth2Authenticator;
import ru.inovus.messaging.server.auth.WebSocketAuthenticator;

@Configuration
public class WebSocketSecurityConfig {

    @Autowired(required = false)
    private TokenStore tokenStore;

    @Bean
    @ConditionalOnProperty(prefix = "security", value = "enabled", havingValue = "false", matchIfMissing = true)
    public WebSocketAuthenticator noAuthAuthenticator() {
        return new NoAuthAuthenticator();
    }

    @Bean
    @ConditionalOnProperty(prefix = "security", value = "enabled", havingValue = "true", matchIfMissing = false)
    public WebSocketAuthenticator authenticator() {
        OAuth2Authenticator authenticator = new OAuth2Authenticator();
        authenticator.setTokenStore(tokenStore);
        return authenticator;
    }
}
