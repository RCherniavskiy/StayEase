package org.example.bookingapplication.exception.telegram;

public class InvalidTelegramToken extends RuntimeException {
    public InvalidTelegramToken(String message) {
        super(message);
    }
}
