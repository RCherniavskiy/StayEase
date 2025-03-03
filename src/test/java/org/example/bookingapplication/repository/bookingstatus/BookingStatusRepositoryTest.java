package org.example.bookingapplication.repository.bookingstatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.bookingapplication.model.booking.BookingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingStatusRepositoryTest {
    private static final BookingStatus.BookingStatusName CONFIRMED_BOOKING_STATUS_NAME =
            BookingStatus.BookingStatusName.CONFIRMED;

    private static final BookingStatus.BookingStatusName EXPIRED_BOOKING_STATUS_NAME =
            BookingStatus.BookingStatusName.EXPIRED;

    @Autowired
    private BookingStatusRepository bookingStatusRepository;

    @Test
    @DisplayName("Find confirmed booking status by status name with exist data")
    void findBookingStatusByName_findExistConfirmedBookingStatus_ReturnBookingStatus() {
        BookingStatus bookingStatusByName =
                bookingStatusRepository.findBookingStatusByName(CONFIRMED_BOOKING_STATUS_NAME);
        assertEquals(CONFIRMED_BOOKING_STATUS_NAME, bookingStatusByName.getName());
    }

    @Test
    @DisplayName("Find confirmed booking status by status name with exist data")
    void findBookingStatusByName_findExistExpiredBookingStatus_ReturnBookingStatus() {
        BookingStatus bookingStatusByName =
                bookingStatusRepository.findBookingStatusByName(EXPIRED_BOOKING_STATUS_NAME);
        assertEquals(EXPIRED_BOOKING_STATUS_NAME, bookingStatusByName.getName());
    }
}
