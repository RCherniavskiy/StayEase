package org.example.bookingapplication.testutil;

import org.example.bookingapplication.dto.bookingstatus.response.BookingStatusDto;
import org.example.bookingapplication.model.booking.BookingStatus;

public class BookingStatusSampleUtil {
    public static BookingStatusDto createSampleBookingStatusDto(
            Long id, BookingStatus.BookingStatusName bookingStatusName) {
        BookingStatusDto bookingStatusDto = new BookingStatusDto();
        bookingStatusDto.setId(id);
        bookingStatusDto.setName(bookingStatusName.name());
        return bookingStatusDto;
    }

    public static BookingStatus createSampleBookingStatus(
            Long id, BookingStatus.BookingStatusName bookingStatusName) {
        BookingStatus bookingStatus = new BookingStatus();
        bookingStatus.setId(id);
        bookingStatus.setName(bookingStatusName);
        return bookingStatus;
    }
}
