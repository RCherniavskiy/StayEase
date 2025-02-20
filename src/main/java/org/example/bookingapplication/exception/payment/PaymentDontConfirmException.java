package org.example.bookingapplication.exception.payment;

public class PaymentDontConfirmException extends RuntimeException {
    public PaymentDontConfirmException(String message) {
        super(message);
    }
}
