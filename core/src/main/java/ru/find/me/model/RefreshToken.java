package ru.find.me.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Серверная половина refresh-токена. Сырой токен клиенту НЕ хранится в БД —
 * сохраняется только его SHA-256 хеш (как пароль), чтобы утечка таблицы
 * не давала готовых к использованию токенов.
 *
 * Поддерживает ротацию: при обновлении старая запись помечается {@code revoked=true}
 * (не удаляется), чтобы при повторном предъявлении уже использованного токена
 * можно было обнаружить кражу (reuse detection).
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** SHA-256 от сырого токена в hex (64 символа). */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean revoked;
}
