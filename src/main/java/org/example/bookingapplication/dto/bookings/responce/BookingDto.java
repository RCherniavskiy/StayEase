package org.example.bookingapplication.dto.bookings.responce;

import lombok.Data;
import org.example.bookingapplication.dto.accommodations.response.AccommodationDto;
import org.example.bookingapplication.dto.bookingstatus.response.BookingStatusDto;
import org.example.bookingapplication.dto.checkdate.responce.CheckDateDto;
import org.example.bookingapplication.dto.users.response.UserResponseDto;

@Data
public class BookingDto {
    private Long id;
    private CheckDateDto checkDates;
    private AccommodationDto accommodation;
    private UserResponseDto user;
    private BookingStatusDto status;
}
