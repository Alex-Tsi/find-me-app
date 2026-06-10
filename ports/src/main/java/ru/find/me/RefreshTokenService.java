package ru.find.me;

/**
 * Управление refresh-токенами: выпуск, ротация (с обнаружением повторного
 * использования) и отзыв. Работает с «сырыми» токенами на входе/выходе,
 * в хранилище кладёт только их хеши.
 */
public interface RefreshTokenService {

    /**
     * Выпускает новый refresh-токен для пользователя.
     *
     * @return сырой токен (его нужно отдать клиенту; в БД он не хранится)
     */
    String issue(long userId);

    /**
     * Проверяет сырой токен и ротирует его: помечает старый отозванным и выпускает новый.
     * При предъявлении уже отозванного токена считает это кражей и отзывает все токены
     * пользователя.
     *
     * @return пара {userId, новый сырой токен}
     * @throws InvalidRefreshTokenException если токен неизвестен, просрочен или повторно использован
     */
    RotationResult rotate(String rawToken);

    /** Отзывает конкретный токен (logout). Неизвестный токен игнорируется. */
    void revoke(String rawToken);

    record RotationResult(long userId, String newRawToken) {
    }
}
