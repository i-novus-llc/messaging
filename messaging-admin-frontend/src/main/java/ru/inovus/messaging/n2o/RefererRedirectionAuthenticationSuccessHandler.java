package ru.inovus.messaging.n2o;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * @author RMakhmutov
 * @since 29.03.2019
 */
public class RefererRedirectionAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    public RefererRedirectionAuthenticationSuccessHandler() {
        super();
        setUseReferer(true);
    }
}