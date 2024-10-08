package ru.inovus.messaging.n2o;

import net.n2oapp.security.auth.OpenIdSecurityCustomizer;
import net.n2oapp.security.auth.SpringUserContextWithToken;
import net.n2oapp.security.auth.context.SpringSecurityUserContext;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import static java.util.Objects.nonNull;

@Configuration
public class SecurityConfig extends OpenIdSecurityCustomizer {

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeHttpRequests(authorizeHttpRequest -> authorizeHttpRequest.anyRequest().authenticated());
        http.oauth2Login(Customizer.withDefaults());
    }

    @Override
    public SpringSecurityUserContext springSecurityUserContext() {
        return new SpringUserContextWithToken() {
            @Override
            public Object get(String param) {
                if ("token".equals(param)) {
                    SecurityContext context = SecurityContextHolder.getContext();
                    if (context != null) {
                        Authentication authentication = context.getAuthentication();
                        if (authentication != null) {
                            return ((OidcUser) authentication.getPrincipal()).getIdToken().getTokenValue();
                        }
                    }
                }
                return super.get(param);
            }
        };
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        //add token to header restTemplate in n2o restDataProviderEngine
        return new RestTemplateBuilder().additionalInterceptors((request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (nonNull(authentication) && authentication.getPrincipal() instanceof DefaultOidcUser) {
                DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
                request.getHeaders().setBearerAuth(principal.getIdToken().getTokenValue());
            }
            return execution.execute(request, body);
        });
    }
}

