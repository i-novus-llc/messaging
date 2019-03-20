package ru.inovus.messaging.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import ru.inovus.messaging.server.config.handler.HttpHandshakeInterceptor;
import ru.inovus.messaging.server.config.handler.SocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SocketHandler socketHandler;

    public WebSocketConfig(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(socketHandler, "/ws")
                .setAllowedOrigins("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor(),
                        new HttpHandshakeInterceptor());
    }
}
