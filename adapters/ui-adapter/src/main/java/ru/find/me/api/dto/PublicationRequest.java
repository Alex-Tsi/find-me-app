package ru.find.me.api.dto;

import jakarta.validation.constraints.NotBlank;

/** Тело создания/обновления публикации. Файл загружается отдельно ({@code /api/upload}). */
public record PublicationRequest(
        @NotBlank String title,
        String description,
        String tags,
        String motivations,
        String rewards,
        String whoNeed,
        String fileName) {
}
