package ru.find.me.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(@NotBlank String text) {
}
