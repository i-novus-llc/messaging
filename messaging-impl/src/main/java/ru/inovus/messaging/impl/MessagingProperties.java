package ru.inovus.messaging.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(
        prefix = "novus.messaging"
)
public class MessagingProperties {
    private Queue queue;
    private RecipientProvider userRoleProvider;
    private ConfigurableProvider configurableProvider;
    private SecurityAdminProvider securityAdminProvider;

    @Getter
    @Setter
    public static class Queue {
        private String status;
        private String feed;
    }

    public enum RecipientProvider {
        CONFIGURABLE,
        SECURITY,
        CUSTOM
    }

    @Getter
    @Setter
    public static class ConfigurableProvider {
        private String usersUrl;
        private String rolesUrl;
    }

    @Getter
    @Setter
    public static class SecurityAdminProvider {
        private String apiUrl;
    }
}
