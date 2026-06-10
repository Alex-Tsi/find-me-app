package ru.find.me;

/** Refresh-токен неизвестен, просрочен или повторно использован. */
public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
