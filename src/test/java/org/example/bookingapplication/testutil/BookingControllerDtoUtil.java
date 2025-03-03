package org.example.bookingapplication.testutil;

import java.time.LocalDate;
import org.example.bookingapplication.dto.bookings.request.BookingPatchRequestDto;
import org.example.bookingapplication.dto.bookings.request.BookingRequestDto;
import org.example.bookingapplication.dto.bookings.responce.BookingDto;
import org.example.bookingapplication.dto.bookingstatus.response.BookingStatusDto;
import org.example.bookingapplication.dto.checkdate.request.CheckDateRequestDto;
import org.example.bookingapplication.dto.checkdate.responce.CheckDateDto;
import org.example.bookingapplication.model.booking.BookingStatus;

public class BookingControllerDtoUtil {

    public static BookingRequestDto getRequestDto() {
        CheckDateRequestDto checkDateRequestDto = new CheckDateRequestDto();
        checkDateRequestDto.setCheckInDate(LocalDate.now().plusDays(1L));
        checkDateRequestDto.setCheckOutDate(LocalDate.now().plusDays(3L));

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setCheckDates(checkDateRequestDto);
        bookingRequestDto.setAccommodationId(1L);

        return bookingRequestDto;
    }

    public static BookingDto getResponseDto() {
        CheckDateDto checkDateDto = new CheckDateDto();
        checkDateDto.setCheckInDate(LocalDate.now().plusDays(1L));
        checkDateDto.setCheckOutDate(LocalDate.now().plusDays(3L));

        BookingStatusDto statusDto = new BookingStatusDto();
        statusDto.setId(1L);
        statusDto.setName(BookingStatus.BookingStatusName.PENDING.name());

        BookingDto bookingRequestDto = new BookingDto();
        bookingRequestDto.setId(3L);
        bookingRequestDto.setCheckDates(checkDateDto);
        bookingRequestDto.setAccommodation(
                AccommodationControllerDtoUtil.getFirstAccommodationResponseDto());
        bookingRequestDto.setUser(UserControllerDtoUtil.getUserResponseDtoFromDb());
        bookingRequestDto.setStatus(statusDto);

        return bookingRequestDto;
    }

    public static BookingDto getFirstBookingDtoFromDb() {
        CheckDateDto checkDateDto = new CheckDateDto();
        checkDateDto.setCheckInDate(LocalDate.of(2034, 11,30));
        checkDateDto.setCheckOutDate(LocalDate.of(2034, 12, 5));

        BookingStatusDto statusDto = new BookingStatusDto();
        statusDto.setId(1L);
        statusDto.setName(BookingStatus.BookingStatusName.PENDING.name());

        BookingDto bookingRequestDto = new BookingDto();
        bookingRequestDto.setId(1L);
        bookingRequestDto.setCheckDates(checkDateDto);
        bookingRequestDto.setAccommodation(
                AccommodationControllerDtoUtil.getFirstAccommodationResponseDto());
        bookingRequestDto.setUser(UserControllerDtoUtil.getUserResponseDtoFromDb());
        bookingRequestDto.setStatus(statusDto);

        return bookingRequestDto;
    }

    public static BookingPatchRequestDto getBookingPatchRequestDto() {
        CheckDateRequestDto checkDateRequestDto = new CheckDateRequestDto();
        checkDateRequestDto.setCheckInDate(LocalDate.of(2034, 11,11));
        checkDateRequestDto.setCheckOutDate(LocalDate.of(2034, 12,11));

        BookingPatchRequestDto patchRequestDto = new BookingPatchRequestDto();
        patchRequestDto.setCheckDates(checkDateRequestDto);
        patchRequestDto.setAccommodationId(1L);
        return patchRequestDto;
    }
}
