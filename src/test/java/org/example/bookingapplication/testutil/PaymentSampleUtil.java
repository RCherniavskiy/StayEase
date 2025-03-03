package org.example.bookingapplication.testutil;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.example.bookingapplication.dto.payment.responce.PaymentDto;
import org.example.bookingapplication.dto.payment.responce.PaymentInfoDto;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.payment.Payment;
import org.example.bookingapplication.model.payment.PaymentStatus;

public class PaymentSampleUtil {
    public static Payment createSamplePayment(Long param) {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatusSampleUtil
                .createSamplePaymentStatus(1L, PaymentStatus.PaymentStatusName.PENDING));
        Booking sampleBooking = BookingSampleUtil.createSampleBooking(param);
        sampleBooking.getCheckDates().setCheckInDate(LocalDate.now().plusDays(1));
        sampleBooking.getCheckDates().setCheckOutDate(LocalDate.now().plusDays(2));
        payment.setBooking(sampleBooking);
        payment.setSessionId("sessionId" + param);
        payment.setSessionUrl("/session-test-payment-url");
        payment.setAmount(sampleBooking.getAccommodation().getDailyRate()
                .multiply(BigDecimal.valueOf(100L)));
        return payment;
    }

    public static PaymentInfoDto createSamplePaymentInfoDto(Long param) {
        Payment samplePayment = createSamplePayment(param);
        PaymentInfoDto paymentDto = new PaymentInfoDto();
        paymentDto.setStatus(PaymentStatusSampleUtil
                .createSamplePaymentStatusDto(1L, PaymentStatus.PaymentStatusName.PENDING));
        paymentDto.setAmount(samplePayment.getAmount());
        paymentDto.setBookingId(samplePayment.getBooking().getId());
        return paymentDto;
    }

    public static PaymentDto createSamplePaymentDto(Long param) {
        Payment samplePayment = createSamplePayment(param);
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setStatus(PaymentStatusSampleUtil
                .createSamplePaymentStatusDto(1L, PaymentStatus.PaymentStatusName.PENDING));
        paymentDto.setSessionId(samplePayment.getSessionId());
        return paymentDto;
    }
}
