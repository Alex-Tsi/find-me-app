package ru.find.me.api.dto;

/** Один диалог в списке чатов: комната + собеседник. */
public record DialogDto(Long roomId, UserDto companion) {
}
