package ru.find.me.api.dto;

import jakarta.validation.constraints.NotBlank;

/** Тело запроса входа: {@code POST /api/auth/login}. */
public record AuthRequest(
        @NotBlank String username,
        @NotBlank String password) {
}
