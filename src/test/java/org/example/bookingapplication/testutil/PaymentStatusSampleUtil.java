package org.example.bookingapplication.testutil;

import org.example.bookingapplication.dto.paymentstatuses.response.PaymentStatusDto;
import org.example.bookingapplication.model.payment.PaymentStatus;

public class PaymentStatusSampleUtil {
    public static PaymentStatus createSamplePaymentStatus(
            Long id, PaymentStatus.PaymentStatusName statusName) {
        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setId(id);
        paymentStatus.setName(statusName);
        return paymentStatus;
    }

    public static PaymentStatusDto createSamplePaymentStatusDto(
            Long id, PaymentStatus.PaymentStatusName statusName) {
        PaymentStatusDto paymentStatusDto = new PaymentStatusDto();
        paymentStatusDto.setId(id);
        paymentStatusDto.setName(statusName.name());
        return paymentStatusDto;
    }
}
