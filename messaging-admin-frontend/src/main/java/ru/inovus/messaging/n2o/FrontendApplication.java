package ru.inovus.messaging.n2o;

import net.n2oapp.framework.security.auth.oauth2.gateway.GatewayPrincipalExtractor;
import net.n2oapp.security.admin.rest.client.AdminRestClientConfiguration;
import net.n2oapp.security.auth.common.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.i_novus.ms.audit.client.UserAccessor;

@SpringBootApplication
@Import(AdminRestClientConfiguration.class)
public class FrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontendApplication.class, args);
    }

    @Bean
    public UserMessageViewPageNameBinder pageNameBinder() {
        return new UserMessageViewPageNameBinder();
    }

    @Bean
    public GatewayPrincipalExtractor gatewayPrincipalExtractor() {
        return new GatewayPrincipalExtractor();
    }

    @Bean
    public UserAccessor auditUser() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return null;
            }
            User user = (User) authentication.getPrincipal();
            return new ru.i_novus.ms.audit.client.model.User(null, user.getUsername());
        };
    }
}

