package ru.find.me.configuration.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ru.find.me.api.security.WebSocketAuthInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfigurer implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor authInterceptor;

    public WebSocketConfigurer(WebSocketAuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/messenger").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Брокер обслуживает /queue и /topic. Личные сообщения идут через
        // user-destination: клиент подписывается на /user/queue/messages, сервер
        // шлёт convertAndSendToUser(user, "/queue/messages", ...) — Spring резолвит
        // это в /queue/messages-user{session}, который обрабатывает simple broker.
        // (Префикс /user — это userDestinationPrefix по умолчанию, НЕ broker-префикс:
        // включать его в enableSimpleBroker нельзя, иначе доставка не работает.)
        registry.setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker("/queue", "/topic");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authInterceptor);
    }
}
