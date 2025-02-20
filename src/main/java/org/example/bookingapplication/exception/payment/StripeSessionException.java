package org.example.bookingapplication.exception.payment;

public class StripeSessionException extends RuntimeException {
    public StripeSessionException(String message) {
        super(message);
    }
}
