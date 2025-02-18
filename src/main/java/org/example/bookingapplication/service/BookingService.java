package org.example.bookingapplication.service;

import java.util.List;
import org.example.bookingapplication.dto.bookings.request.BookingRequestDto;
import org.example.bookingapplication.dto.bookings.responce.BookingDto;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto save(BookingRequestDto requestDto, String email);

    List<BookingDto> findByUserEmail(String email, Pageable pageable);

    List<BookingDto> findAllBookings(Pageable pageable);

    BookingDto getById(Long id, String email);

    BookingDto updateInfo(Long id, BookingRequestDto requestDto, String email);

    BookingDto cancel(Long id, String email);
}
