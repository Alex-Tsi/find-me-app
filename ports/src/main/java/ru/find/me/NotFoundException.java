package ru.find.me;

/** Запрашиваемая сущность не найдена. На уровне REST маппится в 404. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
