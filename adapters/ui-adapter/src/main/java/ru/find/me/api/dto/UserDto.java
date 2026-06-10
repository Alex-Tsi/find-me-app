package ru.find.me.api.dto;

/** Краткое представление пользователя (без чувствительных полей). */
public record UserDto(Long id, String username) {
}
