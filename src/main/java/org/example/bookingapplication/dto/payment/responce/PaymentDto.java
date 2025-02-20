package org.example.bookingapplication.dto.payment.responce;

import lombok.Data;
import org.example.bookingapplication.dto.paymentstatuses.response.PaymentStatusDto;

@Data
public class PaymentDto {
    private Long id;
    private PaymentStatusDto status;
    private String sessionId;
}
