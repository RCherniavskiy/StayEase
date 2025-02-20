package org.example.bookingapplication.exception.payment;

public class PaymentCancelException extends RuntimeException {
    public PaymentCancelException(String message) {
        super(message);
    }
}
