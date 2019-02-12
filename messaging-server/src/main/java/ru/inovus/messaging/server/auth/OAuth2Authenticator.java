package ru.inovus.messaging.server.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;

public class OAuth2Authenticator implements WebSocketAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2Authenticator.class);

    private TokenStore tokenStore;

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public Authentication authenticate(String authToken) {
        try {
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(authToken);
            if (accessToken.isExpired())
                return null;
            return tokenStore.readAuthentication(accessToken);
        } catch (Exception e) {
            logger.error("Wrong access token", e);
        }
        return null;
    }
}
