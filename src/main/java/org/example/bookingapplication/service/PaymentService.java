package org.example.bookingapplication.service;

import java.util.List;
import org.example.bookingapplication.dto.payment.responce.PaymentDto;
import org.example.bookingapplication.dto.payment.responce.PaymentInfoDto;

public interface PaymentService {
    String createPaymentCheckoutSession(Long id, String email);

    PaymentDto successPayment(String sessionId);

    PaymentDto cancelPaymentAndBooking(String sessionId);

    List<PaymentInfoDto> findPaymentsByUserEmail(String email);

    List<PaymentInfoDto> findAllPayments();
}
