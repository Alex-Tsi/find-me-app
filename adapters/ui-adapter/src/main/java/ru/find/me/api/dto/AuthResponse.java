package ru.find.me.api.dto;

/**
 * Ответ на успешный вход/регистрацию/обновление: короткий access-токен и базовые
 * данные пользователя. Refresh-токен сюда не входит — он уходит в httpOnly cookie.
 */
public record AuthResponse(String accessToken, Long userId, String username) {
}
