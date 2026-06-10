package ru.find.me.api.dto;

public record MessageDto(
        Long id,
        String content,
        Long senderId,
        Long recipientId,
        String senderName,
        String recipientName) {
}
