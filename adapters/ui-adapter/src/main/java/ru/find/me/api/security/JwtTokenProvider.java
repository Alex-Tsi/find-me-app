package ru.find.me.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Генерация и валидация короткоживущих access-токенов JWT (HMAC-SHA256).
 * Долгоживущие refresh-токены живут отдельно ({@code RefreshTokenService}).
 * Настройки (application.yaml):
 * <pre>
 * jwt.secret=...&gt;=32 символов... (в проде — из переменной окружения JWT_SECRET)
 * jwt.access-expiration-ms=900000   # 15 минут
 * </pre>
 */
@Component
public class JwtTokenProvider {

    private static final String CLAIM_USER_ID = "userId";

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret:change-me-please-this-is-a-dev-only-secret-key-32+}") String secret,
            @Value("${jwt.access-expiration-ms:900000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_USER_ID, userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String getUsername(String token) {
        return parse(token).getSubject();
    }

    public Long getUserId(String token) {
        return parse(token).get(CLAIM_USER_ID, Long.class);
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
