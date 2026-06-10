package ru.find.me.api.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.find.me.UserService;

/**
 * Аутентификация WebSocket по JWT. SockJS-handshake — обычный HTTP без заголовка
 * Authorization, поэтому токен приходит в STOMP-фрейме CONNECT (нативный заголовок
 * {@code Authorization: Bearer ...}). Здесь он валидируется один раз на CONNECT,
 * после чего {@code Principal} закрепляется за WS-сессией и доступен во всех
 * последующих кадрах (SEND/SUBSCRIBE) и в {@code @MessageMapping}.
 */
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private static final String HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    public WebSocketAuthInterceptor(JwtTokenProvider tokenProvider,
                                    @Qualifier("userServiceImpl") UserService userService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = resolveToken(accessor.getFirstNativeHeader(HEADER));
            if (token == null || !tokenProvider.isValid(token)) {
                throw new MessagingException("WebSocket: отсутствует или неверный токен");
            }
            UserDetails user = userService.loadUserByUsername(tokenProvider.getUsername(token));
            accessor.setUser(new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()));
        }
        return message;
    }

    private String resolveToken(String header) {
        if (header != null && header.startsWith(BEARER)) {
            return header.substring(BEARER.length());
        }
        return null;
    }
}
