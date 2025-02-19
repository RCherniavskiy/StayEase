package org.example.bookingapplication.exception.telegram;

public class TelegramSendMassageException extends RuntimeException {
    public TelegramSendMassageException(String message) {
        super(message);
    }
}
