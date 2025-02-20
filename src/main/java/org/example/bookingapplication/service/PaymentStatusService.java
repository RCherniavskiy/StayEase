package org.example.bookingapplication.service;

import java.util.List;
import org.example.bookingapplication.dto.paymentstatuses.response.PaymentStatusDto;

public interface PaymentStatusService {
    List<PaymentStatusDto> findAll();
}
