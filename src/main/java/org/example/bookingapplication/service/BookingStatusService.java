package org.example.bookingapplication.service;

import java.util.List;
import org.example.bookingapplication.dto.bookingstatus.response.BookingStatusDto;

public interface BookingStatusService {
    List<BookingStatusDto> findAll();
}
