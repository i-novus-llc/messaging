package ru.inovus.messaging.impl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import ru.inovus.messaging.impl.RecipientProvider;
import ru.inovus.messaging.impl.provider.ConfigurableRecipientProvider;
import ru.inovus.messaging.impl.provider.SecurityAdminRecipientProvider;
import ru.inovus.messaging.impl.provider.UserRestClient;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@Configuration
public class RecipientProviderConfiguration {

    @Bean
    @ConditionalOnProperty(value = "novus.messaging.recipient-provider.type", havingValue = "configurable")
    public RecipientProvider userRoleDataProvider(ResourceLoader resourceLoader,
                                                  @Value("${novus.messaging.mapping-file-location}") String mappingFileLocation,
                                                  @Value("${novus.messaging.user-provider-url}") String userUrl) throws IOException, JAXBException {
        return new ConfigurableRecipientProvider(resourceLoader, mappingFileLocation, userUrl);
    }

    @Configuration
    @ConditionalOnProperty(value = "novus.messaging.recipient-provider.type", havingValue = "security")
    @EnableFeignClients(clients = {UserRestClient.class})
    static public class SecurityAdminConfiguration {

        @Bean
        public RecipientProvider userRoleDataProvider(UserRestClient userRestService) {
            return new SecurityAdminRecipientProvider(userRestService);
        }
    }
}
