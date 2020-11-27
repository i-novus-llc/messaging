package ru.inovus.messaging.server;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.inovus.messaging.impl.SecurityAdminUserRoleProvider;
import ru.inovus.messaging.impl.UserRoleProvider;
import ru.inovus.messaging.server.config.DateMapperConfigurer;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableTransactionManagement
@ComponentScan({"ru.inovus.messaging"})
@EnableJaxRsProxyClient(
        classes = {UserRestService.class, RoleRestService.class},
        address = "${sec.admin.rest.url}")
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    DateMapperConfigurer dateMapperConfigurer() {
        return new DateMapperConfigurer();
    }

    @Bean
    public UserRoleProvider userRoleDataProvider(UserRestService userRestService, RoleRestService roleRestService) {
        return new SecurityAdminUserRoleProvider(userRestService, roleRestService);
    }
}
