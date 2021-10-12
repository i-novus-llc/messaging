package ru.inovus.messaging.channel.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(
        prefix = "novus.messaging.channel.email"
)
public class EmailChannelProperties {
    private String queue = "email-queue";
}
