package org.example.bookingapplication.exception.payment;

public class CantPaidBookingException extends RuntimeException {
    public CantPaidBookingException(String message) {
        super(message);
    }
}
