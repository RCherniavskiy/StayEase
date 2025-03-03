package org.example.bookingapplication.testutil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.example.bookingapplication.dto.bookings.request.BookingRequestDto;
import org.example.bookingapplication.dto.bookings.responce.BookingDto;
import org.example.bookingapplication.dto.checkdate.request.CheckDateRequestDto;
import org.example.bookingapplication.dto.checkdate.responce.CheckDateDto;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.example.bookingapplication.model.booking.CheckDate;

public class BookingSampleUtil {
    public static BookingRequestDto createSampleBookingRequestDto() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setCheckDates(createSampleCheckDateRequestDto());
        return requestDto;
    }

    public static BookingDto createSampleBookingDto(Long param) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setCheckDates(createSampleCheckDateDto());
        bookingDto.setAccommodation(AccommodationSampleUtil.getAccommodationDto(param));
        bookingDto.setUser(UserSampleUtil.createSampleUserResponseDto(param));
        bookingDto.setStatus(BookingStatusSampleUtil
                .createSampleBookingStatusDto(1L, BookingStatus.BookingStatusName.PENDING)
        );
        return bookingDto;
    }

    public static Booking createSampleBooking(Long param) {
        Booking booking = new Booking();
        booking.setCheckDates(createSampleCheckDate());
        booking.setAccommodation(AccommodationSampleUtil.getAccommodationModel(param));
        booking.setUser(UserSampleUtil.createSampleUser(param));
        booking.setStatus(BookingStatusSampleUtil
                .createSampleBookingStatus(1L, BookingStatus.BookingStatusName.PENDING)
        );
        booking.setCreatedAt(LocalDateTime.now());
        return booking;
    }

    private static CheckDateRequestDto createSampleCheckDateRequestDto() {
        CheckDateRequestDto checkDateRequestDto = new CheckDateRequestDto();
        checkDateRequestDto.setCheckInDate(LocalDate.now().plusDays(1));
        checkDateRequestDto.setCheckOutDate(LocalDate.now().plusDays(3));
        return checkDateRequestDto;
    }

    private static CheckDateDto createSampleCheckDateDto() {
        CheckDateDto checkDateDto = new CheckDateDto();
        checkDateDto.setCheckInDate(LocalDate.now().plusDays(1));
        checkDateDto.setCheckOutDate(LocalDate.now().plusDays(3));
        return checkDateDto;
    }

    private static CheckDate createSampleCheckDate() {
        CheckDate checkDate = new CheckDate();
        checkDate.setCheckInDate(LocalDate.now().plusDays(1));
        checkDate.setCheckOutDate(LocalDate.now().plusDays(3));
        return checkDate;
    }
}
