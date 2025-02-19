package org.example.bookingapplication.exception.telegram;

public class TelegramBotException extends RuntimeException {
    public TelegramBotException(String message) {
        super(message);
    }
}
