package ru.inovus.messaging.web.channel.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
@PropertySource("classpath:channel.properties")
public class WebSocketConfiguration extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Value("${novus.messaging.app_prefix}")
    private String appPrefix = "/app";
    @Value("${novus.messaging.end_point}")
    private String endPoint = "/ws";

    @Value("${novus.messaging.public_dest_prefix}")
    private String publicDestPrefix = "/topic";
    @Value("${novus.messaging.private_dest_prefix}")
    private String privateDestPrefix = "/exchange";

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Bean //ping-pong
    public TaskScheduler stompHeartbeatThreadBool() {
        ThreadPoolTaskScheduler p = new ThreadPoolTaskScheduler();
        p.setPoolSize(1);
        p.setThreadNamePrefix("stomp-heartbeat-thread-");
        p.initialize();
        return p;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(appPrefix);
        registry.enableSimpleBroker(publicDestPrefix, privateDestPrefix).setTaskScheduler(stompHeartbeatThreadBool());
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(endPoint).setAllowedOrigins("*").withSockJS();
    }
}







