package ru.find.me.api.dto;

public record ProfileDto(
        Long id,
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String avatar,
        String country,
        String sex,
        String registrationDate,
        String city,
        String address,
        Integer age,
        String description,
        String skills) {
}
