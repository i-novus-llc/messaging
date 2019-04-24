package ru.inovus.messaging.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {


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
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic/", "/queue/", "/exchange/").setTaskScheduler(stompHeartbeatThreadBool());
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }
}
