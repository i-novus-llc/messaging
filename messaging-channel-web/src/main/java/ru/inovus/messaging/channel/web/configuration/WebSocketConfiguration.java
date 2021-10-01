package ru.inovus.messaging.channel.web.configuration;

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

    @Value("${novus.messaging.channel.web.app_prefix:/app}")
    private String appPrefix;
    @Value("${novus.messaging.channel.web.end_point:/ws}")
    private String endPoint;

    @Value("${novus.messaging.channel.web.public_dest_prefix:/topic}")
    private String publicDestPrefix;
    @Value("${novus.messaging.channel.web.private_dest_prefix:/exchange}")
    private String privateDestPrefix;

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Bean
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







