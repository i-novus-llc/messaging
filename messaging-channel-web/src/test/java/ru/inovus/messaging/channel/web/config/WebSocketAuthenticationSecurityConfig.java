package ru.inovus.messaging.channel.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketAuthenticationSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private AuthChannelInterceptorAdapter authChannelInterceptorAdapter;

    public WebSocketAuthenticationSecurityConfig(AuthChannelInterceptorAdapter authChannelInterceptorAdapter) {
        this.authChannelInterceptorAdapter = authChannelInterceptorAdapter;
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
    }

    @Override
    public void configureClientInboundChannel(final ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptorAdapter);
    }
}
