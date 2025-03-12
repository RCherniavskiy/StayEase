package org.example.bookingapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.example.bookingapplication.dto.bookingstatus.response.BookingStatusDto;
import org.example.bookingapplication.mapper.BookingStatusMapper;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.example.bookingapplication.repository.bookingstatus.BookingStatusRepository;
import org.example.bookingapplication.testutil.BookingStatusSampleUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingStatusServiceTest {
    @Mock
    private BookingStatusRepository bookingStatusRepository;
    @Mock
    private BookingStatusMapper bookingStatusMapper;
    @InjectMocks
    private BookingStatusServiceImpl bookingStatusService;

    @Test
    @DisplayName("Find all booking status with exist data")
    void findAll_findExistData_ReturnTwoObjects() {
        // Given: Creation of test data
        BookingStatus bookingStatus1 = BookingStatusSampleUtil
                .createSampleBookingStatus(1L, BookingStatus.BookingStatusName.CANCELED);
        BookingStatus bookingStatus2 = BookingStatusSampleUtil
                .createSampleBookingStatus(2L, BookingStatus.BookingStatusName.PENDING);
        BookingStatusDto bookingStatusDto1 = BookingStatusSampleUtil
                .createSampleBookingStatusDto(1L, BookingStatus.BookingStatusName.CANCELED);
        BookingStatusDto bookingStatusDto2 = BookingStatusSampleUtil
                .createSampleBookingStatusDto(2L, BookingStatus.BookingStatusName.PENDING);

        when(bookingStatusRepository.findAll()).thenReturn(
                List.of(bookingStatus1, bookingStatus2));

        when(bookingStatusMapper.toDto(bookingStatus1)).thenReturn(bookingStatusDto1);
        when(bookingStatusMapper.toDto(bookingStatus2)).thenReturn(bookingStatusDto2);

        // When: Click on the method that is being tested
        List<BookingStatusDto> result = bookingStatusService.findAll();

        // Then: Checking the obtained result
        assertEquals(2, result.size());
        assertEquals(bookingStatusDto1, result.get(0));
        assertEquals(bookingStatusDto2, result.get(1));

        // Verify: Verification of click methods
        verify(bookingStatusRepository, times(1)).findAll();
        verify(bookingStatusMapper, times(1)).toDto(bookingStatus1);
        verify(bookingStatusMapper, times(1)).toDto(bookingStatus2);
        verifyNoMoreInteractions(bookingStatusRepository);
        verifyNoMoreInteractions(bookingStatusMapper);
    }
}
