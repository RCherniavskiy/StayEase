package org.example.bookingapplication.repository.paymentstatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.bookingapplication.model.payment.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class PaymentStatusRepositoryTest {
    private static final PaymentStatus.PaymentStatusName PAID_PAYMENT_STATUS_NAME =
            PaymentStatus.PaymentStatusName.PAID;
    private static final PaymentStatus.PaymentStatusName PENDING_PAYMENT_STATUS_NAME =
            PaymentStatus.PaymentStatusName.PENDING;

    @Autowired
    private PaymentStatusRepository paymentStatusRepository;

    @Test
    @DisplayName("Find paid payment status by status name with exist data")
    void findPaymentStatusByName_findPaidPaymentStatus_ReturnPaymentStatus() {
        PaymentStatus paymentStatusByName
                = paymentStatusRepository.findPaymentStatusByName(PAID_PAYMENT_STATUS_NAME);
        assertEquals(PAID_PAYMENT_STATUS_NAME, paymentStatusByName.getName());
    }

    @Test
    @DisplayName("Find pending payment status by status name with exist data")
    void findPaymentStatusByName_findPendingPaymentStatus_ReturnPaymentStatus() {
        PaymentStatus paymentStatusByName
                = paymentStatusRepository.findPaymentStatusByName(PENDING_PAYMENT_STATUS_NAME);
        assertEquals(PENDING_PAYMENT_STATUS_NAME, paymentStatusByName.getName());
    }
}
