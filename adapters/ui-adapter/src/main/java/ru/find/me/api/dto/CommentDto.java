package ru.find.me.api.dto;

public record CommentDto(Long id, String text, UserDto user) {
}
