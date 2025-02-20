package org.example.bookingapplication.dto.payment.responce;

import java.math.BigDecimal;
import lombok.Data;
import org.example.bookingapplication.dto.paymentstatuses.response.PaymentStatusDto;

@Data
public class PaymentInfoDto {
    private Long id;
    private PaymentStatusDto status;
    private Long bookingId;
    private BigDecimal amount;
}
