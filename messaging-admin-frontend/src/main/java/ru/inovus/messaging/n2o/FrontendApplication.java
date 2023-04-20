package ru.inovus.messaging.n2o;

import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.admin.rest.client.AdminRestClientConfiguration;
import net.n2oapp.security.auth.common.User;
import net.n2oapp.security.auth.common.UserAttributeKeys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import ru.i_novus.ms.audit.client.UserAccessor;

import java.util.Set;

@SpringBootApplication
@Import(AdminRestClientConfiguration.class)
public class FrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontendApplication.class, args);
    }

    @Bean
    public UserService keycloakUserService(UserDetailsService userDetailsService, UserAttributeKeys userAttributeKeys) {
        UserService userService = new UserService(userAttributeKeys, userDetailsService, "keycloak");
        OidcUserService oidcUserService = new OidcUserService();
        oidcUserService.setAccessibleScopes(Set.of("openid"));
        userService.setDelegateOidcUserService(oidcUserService);
        return userService;
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

