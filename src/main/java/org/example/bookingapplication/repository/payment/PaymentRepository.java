package org.example.bookingapplication.repository.payment;

import java.util.List;
import java.util.Optional;
import org.example.bookingapplication.model.payment.Payment;
import org.example.bookingapplication.model.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findPaymentByBookingId(Long bookingId);

    @Query("select p from Payment p where p.sessionId = :sessionId")
    Optional<Payment> findPaymentBySessionId(@Param("sessionId") String sessionId);

    List<Payment> findPaymentByBookingUserEmail(String email);

    List<Payment> findAllByBookingIdInAndStatus_Name(List<Long> bookingIds,
                                                   PaymentStatus.PaymentStatusName statusName);

}
