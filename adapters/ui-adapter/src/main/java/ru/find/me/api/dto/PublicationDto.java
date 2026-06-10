package ru.find.me.api.dto;

import java.util.List;

public record PublicationDto(
        Long id,
        String title,
        String description,
        String tags,
        String fileName,
        String date,
        String motivations,
        String rewards,
        String whoNeed,
        UserDto author,
        List<CommentDto> comments) {
}
