package ru.find.me;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.find.me.dao.RefreshTokenRepo;
import ru.find.me.model.RefreshToken;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32; // 256 бит энтропии

    private final RefreshTokenRepo repo;
    private final long refreshExpirationMs;

    public RefreshTokenServiceImpl(@Qualifier("refreshTokenRepo") RefreshTokenRepo repo,
                                   @Value("${jwt.refresh-expiration-ms:604800000}") long refreshExpirationMs) {
        this.repo = repo;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    @Transactional
    public String issue(long userId) {
        String rawToken = generateRawToken();
        RefreshToken entity = new RefreshToken();
        entity.setTokenHash(hash(rawToken));
        entity.setUserId(userId);
        entity.setCreatedAt(Instant.now());
        entity.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        entity.setRevoked(false);
        repo.save(entity);
        return rawToken;
    }

    @Override
    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public RotationResult rotate(String rawToken) {
        RefreshToken token = repo.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new InvalidRefreshTokenException("Неизвестный refresh-токен"));

        if (token.isRevoked()) {
            // Токен уже был использован/отозван, но кто-то предъявляет его снова —
            // признак кражи. Отзываем все токены пользователя.
            repo.revokeAllForUser(token.getUserId());
            throw new InvalidRefreshTokenException("Повторное использование refresh-токена");
        }
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException("refresh-токен просрочен");
        }

        token.setRevoked(true);
        repo.save(token);

        String newRaw = issue(token.getUserId());
        return new RotationResult(token.getUserId(), newRaw);
    }

    @Override
    @Transactional
    public void revoke(String rawToken) {
        Optional<RefreshToken> token = repo.findByTokenHash(hash(rawToken));
        token.ifPresent(t -> {
            t.setRevoked(true);
            repo.save(t);
        });
    }

    /** Раз в сутки чистим просроченные записи, чтобы таблица не росла бесконечно. */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpired() {
        repo.deleteByExpiresAtBefore(Instant.now());
    }

    private String generateRawToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 недоступен", e);
        }
    }
}
