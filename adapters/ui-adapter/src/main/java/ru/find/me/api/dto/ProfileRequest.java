package ru.find.me.api.dto;

/** Тело обновления профиля (редактируемые поля). registrationDate ставится сервером. */
public record ProfileRequest(
        String firstName,
        String lastName,
        String sex,
        Integer age,
        String country,
        String city,
        String skills,
        String phoneNumber,
        String email,
        String description,
        String address,
        String avatar) {
}
