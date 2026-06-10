package ru.find.me.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Тело запроса регистрации: {@code POST /api/auth/register}. */
public record RegisterRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 4) String password) {
}
