package ru.inovus.messaging.n2o;

import net.n2oapp.security.admin.rest.client.AccountServiceRestClient;
import net.n2oapp.security.auth.OpenIdSecurityCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
public class SecurityConfig extends OpenIdSecurityCustomizer {

    @Value("${access.service.userinfo-url}")
    private String userInfoUri;

    @Autowired
    private AccountServiceRestClient accountServiceRestClient;

    @Autowired
    private UserService userService;

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests().anyRequest().authenticated().and().oauth2Login().userInfoEndpoint(userInfo -> userInfo.oidcUserService(userService));
        //todo 7.x.x security backend
//        ContextUserInfoTokenServices tokenServices = new ContextUserInfoTokenServices(userInfoUri);
//        http.addFilterAfter(new ContextFilter(tokenServices, accountServiceRestClient), FilterSecurityInterceptor.class);
    }
}

