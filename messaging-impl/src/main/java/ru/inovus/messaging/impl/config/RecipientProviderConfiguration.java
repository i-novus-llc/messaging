package ru.inovus.messaging.impl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import ru.inovus.messaging.impl.RecipientProvider;
import ru.inovus.messaging.impl.provider.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@Configuration
public class RecipientProviderConfiguration {

    @Bean
    @ConditionalOnProperty(value = "novus.messaging.recipient-provider.type", havingValue = "configurable")
    public RecipientProvider recipientProvider(@Value("${novus.messaging.recipient-provider.configurable.mapping-file-location}") String mappingFileLocation,
                                               @Value("${novus.messaging.recipient-provider.url}") String recipientProviderUrl,
                                               ResourceLoader resourceLoader) throws IOException, JAXBException {
        return new ConfigurableRecipientProvider(resourceLoader, mappingFileLocation, recipientProviderUrl);
    }

    @Configuration
    @ConditionalOnProperty(value = "novus.messaging.recipient-provider.type", havingValue = "security")
    @EnableFeignClients(clients = {UserRestClient.class, RoleRestClient.class, RegionRestClient.class, OrganizationRestClient.class})
    static public class SecurityAdminConfiguration {

        @Bean
        public RecipientProvider recipientProvider(UserRestClient userRestService, RoleRestClient rolesRestService,
                                                   RegionRestClient regionRestService, OrganizationRestClient organizationRestService) {
            return new SecurityAdminRecipientProvider(userRestService, rolesRestService, regionRestService, organizationRestService);
        }
    }
}
