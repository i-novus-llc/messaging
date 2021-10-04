package ru.inovus.messaging.channel.web.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(
        prefix = "novus.messaging.channel.web"
)
public class WebChannelProperties {
    private String queue;
    private String appPrefix = "/app";
    private String endPoint = "/ws";
    private String publicDestPrefix = "/topic";
    private String privateDestPrefix = "/exchange";
    private Integer messageLifetime = 60;
}
