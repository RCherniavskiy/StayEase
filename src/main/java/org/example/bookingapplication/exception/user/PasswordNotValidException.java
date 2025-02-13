package org.example.bookingapplication.exception.user;

public class PasswordNotValidException extends RuntimeException {
    public PasswordNotValidException(String message) {
        super(message);
    }
}
