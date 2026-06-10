package ru.find.me.api.dto;

/** Ответ загрузки файла: имя, под которым файл сохранён (доступен по /img/{fileName}). */
public record UploadResponse(String fileName) {
}
