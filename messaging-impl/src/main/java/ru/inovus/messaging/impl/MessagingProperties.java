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
    private RecipientProvider recipientProvider;
    private String tenantCode;

    @Getter
    @Setter
    public static class Queue {
        private String status;
        private String feedCount;
    }

    @Getter
    @Setter
    public static class RecipientProvider {
        private RecipientProviderType type;
        private String url;
        private ConfigurableProvider configurable;
    }

    public enum RecipientProviderType {
        CONFIGURABLE,
        SECURITY,
        CUSTOM
    }

    @Getter
    @Setter
    public static class ConfigurableProvider {
        String mappingFileLocation;
    }
}
