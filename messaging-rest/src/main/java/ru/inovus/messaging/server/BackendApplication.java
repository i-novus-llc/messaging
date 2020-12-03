package ru.inovus.messaging.server;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.inovus.messaging.impl.ConfigurableUserRoleProvider;
import ru.inovus.messaging.impl.SecurityAdminUserRoleProvider;
import ru.inovus.messaging.impl.UserRoleProvider;
import ru.inovus.messaging.server.config.DateMapperConfigurer;

import javax.management.modelmbean.XMLParseException;
import javax.xml.bind.JAXBException;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableTransactionManagement
@ComponentScan({"ru.inovus.messaging"})
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    DateMapperConfigurer dateMapperConfigurer() {
        return new DateMapperConfigurer();
    }

    @Bean
    @ConditionalOnProperty(value = "messaging.user-role-provider", havingValue = "configurable")
    public UserRoleProvider userRoleDataProvider() throws XMLParseException, JAXBException {
        return new ConfigurableUserRoleProvider();
    }

    @Configuration
    @ConditionalOnProperty(value = "messaging.user-role-provider", havingValue = "security")
    public class JaxRsProxyClientConfiguration {

        @Configuration
        @EnableJaxRsProxyClient(
                classes = {UserRestService.class, RoleRestService.class},
                address = "${messaging.user-provider-url}")
        public class JaxRsProxyClient {
            @Bean
            @Primary
            public UserRoleProvider userRoleDataProvider(UserRestService userRestService, RoleRestService roleRestService) {
                return new SecurityAdminUserRoleProvider(userRestService, roleRestService);
            }
        }
    }
}