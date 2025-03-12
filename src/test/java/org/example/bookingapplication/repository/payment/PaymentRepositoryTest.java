package org.example.bookingapplication.repository.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.example.bookingapplication.model.payment.Payment;
import org.example.bookingapplication.model.payment.PaymentStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest {
    private static final String ADD_THREE_ACCOMMODATIONS
            = "database/accommodations/insert-accommodations.sql";
    private static final String ADD_TEST_USER
            = "database/users/insert-users.sql";
    private static final String ADD_TEST_BOOKING
            = "database/bookings/insert-booking.sql";
    private static final String ADD_TEST_PAYMENTS
            = "database/payments/insert-two-payments.sql";
    private static final String DELETE_DATA
            = "database/deletes/delete-users-accommodations-bookings-addresses.sql";
    private static final PaymentStatus.PaymentStatusName PAID_PAYMENT_STATUS
            = PaymentStatus.PaymentStatusName.PAID;
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_THREE_ACCOMMODATIONS));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_TEST_USER));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_TEST_BOOKING));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_TEST_PAYMENTS));
        }
    }

    @Test
    @DisplayName("Find payment by session id with non exist data")
    void findPaymentBySessionId_findNotExistPayment_ReturnEmptyOptional() {
        String testSessionId = "testNotExistSessionId";
        Optional<Payment> paymentBySessionId =
                paymentRepository.findPaymentBySessionId(testSessionId);
        assertTrue(paymentBySessionId.isEmpty());
    }

    @Test
    @DisplayName("Find payment by session id with exist data")
    void findPaymentBySessionId_findExistPayment_ReturnPayment() {
        String testSessionId = "testSessionId1";
        Optional<Payment> paymentBySessionId =
                paymentRepository.findPaymentBySessionId(testSessionId);
        assertTrue(paymentBySessionId.isPresent());
        Payment payment = paymentBySessionId.get();
        assertEquals(testSessionId, payment.getSessionId());
    }

    @Test
    @DisplayName("Find payment by bookingId with non exist data")
    void findPaymentByBookingId_findNonExistPayment_ReturnEmptyOptional() {
        Long nonExistBookingId = -1L;
        Optional<Payment> paymentByBookingId =
                paymentRepository.findPaymentByBookingId(nonExistBookingId);
        assertTrue(paymentByBookingId.isEmpty());
    }

    @Test
    @DisplayName("Find payment by bookingId with exist data")
    void findPaymentByBookingId_findExistPayment_ReturnPayment() {
        Long existBookingId = 1L;
        Optional<Payment> paymentByBookingId =
                paymentRepository.findPaymentByBookingId(existBookingId);
        assertTrue(paymentByBookingId.isPresent());
        Payment payment = paymentByBookingId.get();
        assertEquals(existBookingId, payment.getBooking().getId());
    }

    @Test
    @DisplayName("Find payments by user email with non exist data")
    void findPaymentByBookingUserEmail_findNonExistPayment_ReturnEmptyList() {
        String nonExistEmail = "nonExistEmail@i.com";
        List<Payment> paymentsByBookingUserEmail =
                paymentRepository.findPaymentByBookingUserEmail(nonExistEmail);
        assertTrue(paymentsByBookingUserEmail.isEmpty());
    }

    @Test
    @DisplayName("Find payments by user email with exist data")
    void findPaymentByBookingUserEmail_findExistPayment_ReturnListOfPayment() {
        String existEmail = "testUser1@testmail.com";
        List<Payment> paymentsByBookingUserEmail =
                paymentRepository.findPaymentByBookingUserEmail(existEmail);
        assertEquals(2, paymentsByBookingUserEmail.size());
        assertEquals(paymentsByBookingUserEmail.get(0).getBooking().getUser()
                .getEmail(), existEmail);
        assertEquals(paymentsByBookingUserEmail.get(1).getBooking().getUser()
                .getEmail(), existEmail);
    }

    @Test
    @DisplayName("Find payments by bookingId and status with non exist data")
    void findAllByBookingIdInAndStatus_Name_findNonExistPayment_ReturnEmptyList() {
        List<Long> nonExistBookingIds = List.of(-1L, -2L);
        List<Payment> paymentsByBookingIdAndStatus =
                paymentRepository.findAllByBookingIdInAndStatus_Name(
                        nonExistBookingIds, PAID_PAYMENT_STATUS);
        assertTrue(paymentsByBookingIdAndStatus.isEmpty());
    }

    @Test
    @DisplayName("Find payments by bookingId and status with exist data")
    void findAllByBookingIdInAndStatus_Name_findExistPayment_ReturnListWithOnePayment() {
        List<Long> existBookingIds = List.of(1L, 2L);
        List<Payment> paymentsByBookingIdAndStatus =
                paymentRepository.findAllByBookingIdInAndStatus_Name(
                        existBookingIds, PAID_PAYMENT_STATUS);
        assertEquals(1, paymentsByBookingIdAndStatus.size());
        assertEquals(2, paymentsByBookingIdAndStatus.get(0).getBooking().getId());
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(DELETE_DATA));
        }
    }
}
