package ru.inovus.messaging.impl.config;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import ru.inovus.messaging.impl.UserRoleProvider;
import ru.inovus.messaging.impl.provider.ConfigurableUserRoleProvider;
import ru.inovus.messaging.impl.provider.RoleRestClient;
import ru.inovus.messaging.impl.provider.SecurityAdminUserRoleProvider;
import ru.inovus.messaging.impl.provider.UserRestClient;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@Configuration
public class UserRoleDataProviderConfiguration {

    @Bean
    @ConditionalOnProperty(value = "novus.messaging.user-role-provider", havingValue = "configurable")
    public UserRoleProvider userRoleDataProvider(ResourceLoader resourceLoader,
                                                 @Value("${novus.messaging.mapping-file-location}") String mappingFileLocation,
                                                 @Value("${novus.messaging.user-provider-url}") String userUrl,
                                                 @Value("${novus.messaging.role-provider-url}") String roleUrl) throws IOException, JAXBException {
        return new ConfigurableUserRoleProvider(resourceLoader, mappingFileLocation, userUrl, roleUrl);
    }

    @Configuration
    @ConditionalOnProperty(value = "novus.messaging.user-role-provider", havingValue = "security")
    @EnableFeignClients(clients = {UserRestClient.class, RoleRestClient.class})
    static public class SecurityAdminConfiguration {

        @Bean
        public UserRoleProvider userRoleDataProvider(UserRestClient userRestService,
                                                     RoleRestClient roleRestService) {
            return new SecurityAdminUserRoleProvider(userRestService, roleRestService);
        }
    }
}
