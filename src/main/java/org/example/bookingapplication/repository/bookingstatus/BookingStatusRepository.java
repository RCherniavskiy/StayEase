package org.example.bookingapplication.repository.bookingstatus;

import org.example.bookingapplication.model.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingStatusRepository extends JpaRepository<BookingStatus, Long> {
    BookingStatus findBookingStatusByName(BookingStatus.BookingStatusName name);
}
