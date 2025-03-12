package org.example.bookingapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import org.example.bookingapplication.dto.bookings.request.BookingRequestDto;
import org.example.bookingapplication.dto.bookings.responce.BookingDto;
import org.example.bookingapplication.dto.bookingstatus.response.BookingStatusDto;
import org.example.bookingapplication.dto.checkdate.request.CheckDateRequestDto;
import org.example.bookingapplication.exception.booking.BookingInfoException;
import org.example.bookingapplication.exception.booking.InvalidDateException;
import org.example.bookingapplication.exception.repository.EntityAlreadyExistsException;
import org.example.bookingapplication.exception.repository.EntityNotFoundException;
import org.example.bookingapplication.exception.user.UserDontHavePermissions;
import org.example.bookingapplication.mapper.BookingMapper;
import org.example.bookingapplication.mapper.CheckDateMapper;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.example.bookingapplication.model.booking.CheckDate;
import org.example.bookingapplication.repository.accommodation.AccommodationRepository;
import org.example.bookingapplication.repository.booking.BookingRepository;
import org.example.bookingapplication.repository.bookingstatus.BookingStatusRepository;
import org.example.bookingapplication.repository.user.UserRepository;
import org.example.bookingapplication.telegram.BookingBot;
import org.example.bookingapplication.testutil.AccommodationSampleUtil;
import org.example.bookingapplication.testutil.BookingSampleUtil;
import org.example.bookingapplication.testutil.BookingStatusSampleUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingStatusRepository bookingStatusRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CheckDateMapper checkDateMapper;
    @Mock
    private BookingBot bookingBot;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("Save booking with not valid check dates")
    void save_saveBookingWithValidData_ReturnBookingDto() {
        // Given - Preparing test data
        BookingRequestDto requestDto = BookingSampleUtil.createSampleBookingRequestDto();
        Booking booking = BookingSampleUtil.createSampleBooking(1L);
        Accommodation accommodation = AccommodationSampleUtil.getAccommodationModel(1L);
        BookingDto responseDto = BookingSampleUtil.createSampleBookingDto(1L);

        when(bookingMapper.toModelWithoutStatusAndUser(requestDto)).thenReturn(booking);
        when(accommodationRepository.findById(requestDto.getAccommodationId()))
                .thenReturn(Optional.of(accommodation));
        when(checkDateMapper.toModel(requestDto.getCheckDates()))
                .thenReturn(booking.getCheckDates());
        when(bookingRepository.isDatesAvailableForAccommodation(
                accommodation.getId(),
                booking.getCheckDates().getCheckInDate(),
                booking.getCheckDates().getCheckOutDate())
        ).thenReturn(0L);
        when(userRepository.findUserByEmail(booking.getUser().getEmail()))
                .thenReturn(Optional.of(booking.getUser()));
        when(bookingStatusRepository.findBookingStatusByName(booking.getStatus().getName()))
                .thenReturn(booking.getStatus());
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(responseDto);

        // When - Executing the method under test
        BookingDto result = bookingService.save(requestDto, booking.getUser().getEmail());

        // Then - Verifying the result
        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(bookingMapper, times(1)).toModelWithoutStatusAndUser(requestDto);
        verify(accommodationRepository, times(1)).findById(requestDto.getAccommodationId());
        verify(checkDateMapper, times(1)).toModel(requestDto.getCheckDates());
        verify(bookingRepository, times(1))
                .isDatesAvailableForAccommodation(
                        accommodation.getId(),
                        booking.getCheckDates().getCheckInDate(),
                        booking.getCheckDates().getCheckOutDate()
                );
        verify(userRepository, times(1)).findUserByEmail(booking.getUser().getEmail());
        verify(bookingStatusRepository, times(1))
                .findBookingStatusByName(booking.getStatus().getName());
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingMapper, times(1)).toDto(booking);

        verifyNoMoreInteractions(
                userRepository,
                accommodationRepository,
                bookingRepository,
                bookingStatusRepository,
                bookingMapper,
                checkDateMapper
        );
    }

    @Test
    @DisplayName("Save booking with not valid accommodation id data")
    void save_saveBookingWithNotValidAccommodationId_ThrowException() {
        // Given - Preparing test data
        BookingRequestDto requestDto = BookingSampleUtil.createSampleBookingRequestDto();
        requestDto.setAccommodationId(-1L);
        Booking booking = BookingSampleUtil.createSampleBooking(1L);

        when(bookingMapper.toModelWithoutStatusAndUser(requestDto)).thenReturn(booking);
        when(accommodationRepository.findById(requestDto.getAccommodationId()))
                .thenReturn(Optional.empty());

        // When & Then - Asserting that an exception is thrown
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.save(requestDto, booking.getUser().getEmail()));

        verify(bookingMapper, times(1)).toModelWithoutStatusAndUser(requestDto);
        verify(accommodationRepository, times(1)).findById(requestDto.getAccommodationId());

        verifyNoMoreInteractions(
                userRepository,
                accommodationRepository,
                bookingRepository,
                bookingStatusRepository,
                bookingMapper,
                checkDateMapper
        );
    }

    @Test
    @DisplayName("Save booking with other booking in this day data")
    void save_saveBookingWithOtherBookings_ThrowException() {
        // Given - Preparing test data
        BookingRequestDto requestDto = BookingSampleUtil.createSampleBookingRequestDto();
        Booking booking = BookingSampleUtil.createSampleBooking(1L);
        Accommodation accommodation = AccommodationSampleUtil.getAccommodationModel(1L);

        when(bookingMapper.toModelWithoutStatusAndUser(requestDto)).thenReturn(booking);
        when(accommodationRepository.findById(requestDto.getAccommodationId()))
                .thenReturn(Optional.of(accommodation));
        when(checkDateMapper.toModel(requestDto.getCheckDates()))
                .thenReturn(booking.getCheckDates());
        when(bookingRepository.isDatesAvailableForAccommodation(
                accommodation.getId(),
                booking.getCheckDates().getCheckInDate(),
                booking.getCheckDates().getCheckOutDate())
        ).thenReturn(2L);

        // When & Then - Expecting an exception
        assertThrows(EntityAlreadyExistsException.class,
                () -> bookingService.save(requestDto, booking.getUser().getEmail()));

        // Verifying interactions
        verify(bookingMapper, times(1)).toModelWithoutStatusAndUser(requestDto);
        verify(accommodationRepository, times(1)).findById(requestDto.getAccommodationId());
        verify(checkDateMapper, times(1)).toModel(requestDto.getCheckDates());
        verify(bookingRepository, times(1)).isDatesAvailableForAccommodation(
                accommodation.getId(),
                booking.getCheckDates().getCheckInDate(),
                booking.getCheckDates().getCheckOutDate()
        );

        verifyNoMoreInteractions(
                userRepository,
                accommodationRepository,
                bookingRepository,
                bookingStatusRepository,
                bookingMapper,
                checkDateMapper
        );
    }

    @Test
    @DisplayName("Save booking with not valid user email")
    void save_saveBookingWithNotValidEmailData_ThrowException() {
        // Given - Preparing test data
        BookingRequestDto requestDto = BookingSampleUtil.createSampleBookingRequestDto();
        Booking booking = BookingSampleUtil.createSampleBooking(1L);
        Accommodation accommodation = AccommodationSampleUtil.getAccommodationModel(1L);

        when(bookingMapper.toModelWithoutStatusAndUser(requestDto)).thenReturn(booking);
        when(accommodationRepository.findById(requestDto.getAccommodationId()))
                .thenReturn(Optional.of(accommodation));
        when(checkDateMapper.toModel(requestDto.getCheckDates()))
                .thenReturn(booking.getCheckDates());
        when(bookingRepository.isDatesAvailableForAccommodation(
                accommodation.getId(),
                booking.getCheckDates().getCheckInDate(),
                booking.getCheckDates().getCheckOutDate())
        ).thenReturn(0L);
        when(userRepository.findUserByEmail(booking.getUser().getEmail()))
                .thenReturn(Optional.empty());

        // When & Then - Expecting an exception
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.save(requestDto, booking.getUser().getEmail()));

        // Verifying interactions
        verify(bookingMapper, times(1)).toModelWithoutStatusAndUser(requestDto);
        verify(accommodationRepository, times(1)).findById(requestDto.getAccommodationId());
        verify(checkDateMapper, times(1)).toModel(requestDto.getCheckDates());
        verify(bookingRepository, times(1))
                .isDatesAvailableForAccommodation(
                        accommodation.getId(),
                        booking.getCheckDates().getCheckInDate(),
                        booking.getCheckDates().getCheckOutDate()
                );
        verify(userRepository, times(1)).findUserByEmail(booking.getUser().getEmail());

        verifyNoMoreInteractions(
                userRepository,
                accommodationRepository,
                bookingRepository,
                bookingStatusRepository,
                bookingMapper,
                checkDateMapper
        );
    }

    @Test
    @DisplayName("Save booking with not valid check dates")
    void save_saveBookingWithNotValidCheckDate_ThrowException() {
        // Given - Preparing test data with incorrect check-in and check-out dates
        BookingRequestDto requestDto = BookingSampleUtil.createSampleBookingRequestDto();

        CheckDateRequestDto notValidCheckDateDto = new CheckDateRequestDto();
        notValidCheckDateDto.setCheckInDate(LocalDate.now());
        notValidCheckDateDto.setCheckInDate(LocalDate.now().minusDays(1));
        requestDto.setCheckDates(notValidCheckDateDto);

        Booking booking = BookingSampleUtil.createSampleBooking(1L);

        CheckDate notValidCheckDate = new CheckDate();
        notValidCheckDate.setCheckInDate(LocalDate.now());
        notValidCheckDate.setCheckInDate(LocalDate.now().minusDays(1));
        booking.setCheckDates(notValidCheckDate);

        when(bookingMapper.toModelWithoutStatusAndUser(requestDto)).thenReturn(booking);

        // When & Then - Expecting an exception
        assertThrows(InvalidDateException.class,
                () -> bookingService.save(requestDto, booking.getUser().getEmail()));

        // Verifying interactions
        verify(bookingMapper, times(1)).toModelWithoutStatusAndUser(requestDto);

        verifyNoMoreInteractions(
                userRepository,
                accommodationRepository,
                bookingRepository,
                bookingStatusRepository,
                bookingMapper,
                checkDateMapper
        );
    }

    @Test
    @DisplayName("Cancel booking with valid data")
    void cancel_cancelBookingWithValidData_ReturnBookingDto() {
        // Given: Creating test data
        Booking booking = BookingSampleUtil.createSampleBooking(1L);
        BookingStatus canceledBookingStatus = BookingStatusSampleUtil
                .createSampleBookingStatus(3L, BookingStatus.BookingStatusName.CANCELED);
        BookingDto responseDto = BookingSampleUtil.createSampleBookingDto(1L);
        BookingStatusDto canceledBookingStatusDto = BookingStatusSampleUtil
                .createSampleBookingStatusDto(3L, BookingStatus.BookingStatusName.CANCELED);
        responseDto.setStatus(canceledBookingStatusDto);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingStatusRepository.findBookingStatusByName(canceledBookingStatus.getName()))
                .thenReturn(canceledBookingStatus);
        booking.setStatus(canceledBookingStatus);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(responseDto);

        booking.setStatus(BookingStatusSampleUtil
                .createSampleBookingStatus(2L, BookingStatus.BookingStatusName.PENDING));

        // When: call the cancel method
        BookingDto result = bookingService.cancel(1L, booking.getUser().getEmail());

        // Then: check result and interactions
        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingStatusRepository, times(1))
                .findBookingStatusByName(canceledBookingStatus.getName());
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingMapper, times(1)).toDto(booking);

        verifyNoMoreInteractions(
                userRepository,
                accommodationRepository,
                bookingRepository,
                bookingStatusRepository,
                bookingMapper,
                checkDateMapper
        );
    }

    @Test
    @DisplayName("Cancel booking with not valid booking id")
    void cancel_cancelBookingWithNotValidId_ThrowException() {
        // Given: non-existent booking ID
        Booking booking = BookingSampleUtil.createSampleBooking(1L);

        when(bookingRepository.findById(-1L)).thenReturn(Optional.empty());

        // When and Then: Call the cancel method and check if an exception is thrown
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.cancel(-1L, booking.getUser().getEmail()));

        verify(bookingRepository, times(1)).findById(-1L);
        verifyNoMoreInteractions(
                userRepository,
                accommodationRepository,
                bookingRepository,
                bookingStatusRepository,
                bookingMapper,
                checkDateMapper
        );
    }

    @Test
    @DisplayName("Cancel booking with not valid user email")
    void cancel_cancelBookingWithNotValidUserEmail_ThrowException() {
        // Given: existing booking, but invalid user email
        Booking booking = BookingSampleUtil.createSampleBooking(1L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // When and Then: Call the cancel method and check if an exception is thrown
        assertThrows(UserDontHavePermissions.class,
                () -> bookingService.cancel(1L, "not found email"));

        verify(bookingRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(
                userRepository,
                accommodationRepository,
                bookingRepository,
                bookingStatusRepository,
                bookingMapper,
                checkDateMapper
        );
    }

    @Test
    @DisplayName("Cancel booking with not valid booking status")
    void cancel_cancelBookingWithNotValidBookingStatus_ThrowException() {
        // Given
        Booking booking = BookingSampleUtil.createSampleBooking(1L);
        BookingStatus confirmedBookingStatus = BookingStatusSampleUtil
                .createSampleBookingStatus(3L, BookingStatus.BookingStatusName.CONFIRMED);
        booking.setStatus(confirmedBookingStatus);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // When & Then
        assertThrows(BookingInfoException.class,
                () -> bookingService.cancel(1L, booking.getUser().getEmail()));

        verify(bookingRepository, times(1)).findById(1L);

        verifyNoMoreInteractions(
                userRepository,
                accommodationRepository,
                bookingRepository,
                bookingStatusRepository,
                bookingMapper,
                checkDateMapper
        );
    }

    @Test
    @DisplayName("Get booking by id with valid data")
    void getById_GetBookingByIdWithValidData_ReturnBooking() {
        // Given
        Booking sampleBooking = BookingSampleUtil.createSampleBooking(1L);
        sampleBooking.setId(1L);
        BookingDto sampleBookingDto = BookingSampleUtil.createSampleBookingDto(1L);
        sampleBookingDto.setId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(sampleBooking));
        when(bookingMapper.toDto(sampleBooking)).thenReturn(sampleBookingDto);

        // When
        BookingDto result = bookingService.getById(1L, sampleBooking.getUser().getEmail());

        // Then
        assertNotNull(result);
        assertEquals(sampleBookingDto, result);

        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingMapper, times(1)).toDto(sampleBooking);
        verifyNoMoreInteractions(
                userRepository,
                accommodationRepository,
                bookingRepository,
                bookingStatusRepository,
                bookingMapper,
                checkDateMapper
        );
    }

    @Test
    @DisplayName("Get booking by id with not valid id")
    void getById_GetBookingByIdWithNotValidId_throwException() {
        // Given
        when(bookingRepository.findById(-1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getById(-1L, "sample@email.com"));

        verify(bookingRepository, times(1)).findById(-1L);
        verifyNoMoreInteractions(
                userRepository,
                accommodationRepository,
                bookingRepository,
                bookingStatusRepository,
                bookingMapper,
                checkDateMapper
        );
    }

    @Test
    @DisplayName("Get booking by id with not valid email")
    void getById_GetBookingByIdWithNotValidEmail_ThrowException() {
        // Given
        Booking sampleBooking = BookingSampleUtil.createSampleBooking(1L);
        sampleBooking.setId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(sampleBooking));

        // When & Then
        assertThrows(UserDontHavePermissions.class,
                () -> bookingService.getById(1L, "dontHave@email.com"));

        verify(bookingRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(
                userRepository,
                accommodationRepository,
                bookingRepository,
                bookingStatusRepository,
                bookingMapper,
                checkDateMapper
        );
    }
}
