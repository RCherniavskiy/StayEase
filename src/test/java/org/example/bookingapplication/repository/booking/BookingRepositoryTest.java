package org.example.bookingapplication.repository.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest {
    private static final String ADD_THREE_ACCOMMODATIONS
            = "database/accommodations/insert-accommodations.sql";
    private static final String ADD_TEST_USER
            = "database/users/insert-users.sql";
    private static final String ADD_TEST_BOOKING
            = "database/bookings/insert-booking.sql";
    private static final String DELETE_DATA
            = "database/deletes/delete-users-accommodations-bookings-addresses.sql";
    @Autowired
    private BookingRepository bookingRepository;

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
        }
    }

    @Test
    @DisplayName("Find booking by email with empty data")
    void findAllByUser_Email_getBookingByDontValidUserEmail_ReturnEmptyList() {
        String testEmail = "email@i.test";
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<Booking> allByUserEmail = bookingRepository.findAllByUser_Email(testEmail, pageable);
        assertTrue(allByUserEmail.isEmpty());
    }

    @Test
    @DisplayName("Find booking by email with 2 bookings")
    @Transactional
    void findAllByUser_Email_getBookingByValidUserEmail_ReturnBookings() {
        String testEmail = "testUser1@testmail.com";
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> allByUserEmail = bookingRepository.findAllByUser_Email(testEmail, pageable);
        assertEquals(2L, allByUserEmail.size());
        assertEquals(testEmail, allByUserEmail.get(0).getUser().getEmail());
    }

    @Test
    @DisplayName("Find booking by check in date with non exist data")
    @Transactional
    void findAllByCheckInDate_getBookingByNonExistCheckInDate_OneBooking() {
        LocalDate nonExistCheckInDate = LocalDate.of(1999, 10, 20);
        BookingStatus.BookingStatusName pending = BookingStatus.BookingStatusName.PENDING;
        List<Booking> allByUserCheckInDate =
                bookingRepository.findAllByCheckInDate(nonExistCheckInDate, pending);
        assertEquals(0L, allByUserCheckInDate.size());
    }

    @Test
    @DisplayName("Find booking by check in date with exist data")
    @Transactional
    void findAllByCheckInDate_getBookingByCheckInDate_OneBooking() {
        LocalDate checkInDate = LocalDate.of(2034, 11, 30);
        BookingStatus.BookingStatusName pending = BookingStatus.BookingStatusName.PENDING;
        List<Booking> allByUserCheckInDate =
                bookingRepository.findAllByCheckInDate(checkInDate, pending);
        assertEquals(1L, allByUserCheckInDate.size());
        assertEquals(checkInDate, allByUserCheckInDate.get(0).getCheckDates().getCheckInDate());
    }

    @Test
    @DisplayName("Find booking by check out date with non exist data")
    @Transactional
    void findAllByCheckOutDate_getBookingByNonExistCheckOutDate_OneBooking() {
        LocalDate nonExistCheckInDate = LocalDate.of(1999, 10, 20);
        BookingStatus.BookingStatusName pending = BookingStatus.BookingStatusName.PENDING;
        List<Booking> allByUserCheckInDate =
                bookingRepository.findAllByCheckOutDate(nonExistCheckInDate, pending);
        assertEquals(0L, allByUserCheckInDate.size());
    }

    @Test
    @DisplayName("Find booking by check out date with exist data")
    @Transactional
    void findAllByCheckOutDate_getBookingByCheckCheckOutDate_OneBooking() {
        LocalDate checkOutDate = LocalDate.of(2034, 11, 05);
        BookingStatus.BookingStatusName expired = BookingStatus.BookingStatusName.EXPIRED;
        List<Booking> allByUserCheckInDate =
                bookingRepository.findAllByCheckOutDate(checkOutDate, expired);
        assertEquals(1L, allByUserCheckInDate.size());
        assertEquals(checkOutDate, allByUserCheckInDate.get(0).getCheckDates().getCheckOutDate());
    }

    @Test
    @DisplayName("Find booking by created at date with exist data")
    @Transactional
    void findAllByCreatedAtDate_getBookingByCreatedAtDate_OneBooking() {
        LocalDateTime dateTime = LocalDateTime.of(2034, 10, 10, 12, 0,0);
        BookingStatus.BookingStatusName expired = BookingStatus.BookingStatusName.EXPIRED;
        List<Booking> allByUserCheckInDate =
                bookingRepository.findAllByCreatedAtAndStatus(dateTime, expired);
        assertEquals(1L, allByUserCheckInDate.size());
        assertTrue(allByUserCheckInDate.get(0).getCreatedAt().isBefore(dateTime));
    }

    @Test
    @DisplayName("Find booking by created at date with non exist data")
    @Transactional
    void findAllByCreatedAtDate_getBookingNonExistByCreatedAtDate_Empty() {
        LocalDateTime dateTime = LocalDateTime.of(1990, 10, 10, 12, 0,0);
        BookingStatus.BookingStatusName expired = BookingStatus.BookingStatusName.EXPIRED;
        List<Booking> allByUserCheckInDate =
                bookingRepository.findAllByCreatedAtAndStatus(dateTime, expired);
        assertEquals(0L, allByUserCheckInDate.size());
    }

    @Test
    @DisplayName("Find booking by created at date with non exist data")
    @Transactional
    void isDatesAvailableForAccommodation_findWithExistBookings_ReturnCountOne() {
        Long accommodationId = 1L;
        LocalDate checkInDate = LocalDate.of(2034, 11, 30);
        LocalDate checkOutDate = LocalDate.of(2034, 12, 5);
        Long count = bookingRepository
                .isDatesAvailableForAccommodation(accommodationId, checkInDate, checkOutDate);
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Find booking by created at date with non exist data")
    @Transactional
    void isDatesAvailableForAccommodation_findWithNonExistBookings_ReturnCountZero() {
        Long accommodationId = 1L;
        LocalDate checkInDate = LocalDate.of(2031, 11, 30);
        LocalDate checkOutDate = LocalDate.of(2031, 12, 5);
        Long count = bookingRepository
                .isDatesAvailableForAccommodation(accommodationId, checkInDate, checkOutDate);
        assertEquals(0L, count);
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
