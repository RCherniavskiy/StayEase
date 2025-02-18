package org.example.bookingapplication.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.bookingstatus.response.BookingStatusDto;
import org.example.bookingapplication.mapper.BookingStatusMapper;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.example.bookingapplication.repository.bookingstatus.BookingStatusRepository;
import org.example.bookingapplication.service.BookingStatusService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingStatusServiceImpl implements BookingStatusService {
    private final BookingStatusRepository bookingStatusRepository;
    private final BookingStatusMapper bookingStatusMapper;

    @Override
    public List<BookingStatusDto> findAll() {
        List<BookingStatus> listOfTypes = bookingStatusRepository.findAll();
        return listOfTypes.stream()
                .map(bookingStatusMapper::toDto)
                .toList();
    }
}
