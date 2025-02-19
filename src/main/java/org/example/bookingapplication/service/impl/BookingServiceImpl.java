package org.example.bookingapplication.service.impl;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.bookings.request.BookingRequestDto;
import org.example.bookingapplication.dto.bookings.responce.BookingDto;
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
import org.example.bookingapplication.model.user.User;
import org.example.bookingapplication.repository.accommodation.AccommodationRepository;
import org.example.bookingapplication.repository.booking.BookingRepository;
import org.example.bookingapplication.repository.bookingstatus.BookingStatusRepository;
import org.example.bookingapplication.repository.user.UserRepository;
import org.example.bookingapplication.service.BookingService;
import org.example.bookingapplication.telegram.BookingBot;
import org.example.bookingapplication.telegram.util.NotificationConfigurator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final Long DONT_HAVE_AVAILABLE_VALUE = 0L;
    private static final BookingStatus.BookingStatusName PENDING_STATUS
            = BookingStatus.BookingStatusName.PENDING;
    private static final BookingStatus.BookingStatusName CANCELED_STATUS
            = BookingStatus.BookingStatusName.CANCELED;
    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;
    private final BookingRepository bookingRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final BookingMapper bookingMapper;
    private final CheckDateMapper checkDateMapper;
    private final BookingBot bookingBot;

    @Override
    @Transactional
    public BookingDto save(BookingRequestDto requestDto, String email) {
        Booking booking = bookingMapper.toModelWithoutStatusAndUser(requestDto);
        isCheckDateValid(booking.getCheckDates());
        setAccommodationIfIsFree(booking, requestDto);
        setUserToBooking(booking, email);
        setBookingStatusToBooking(booking, PENDING_STATUS);
        Booking savedBooking = bookingRepository.save(booking);
        String message = NotificationConfigurator.bookingCreated(booking);
        bookingBot.sendMessage(email, message);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto cancel(Long id, String email) {
        Booking booking = getByIdIfUserHavePermission(id, email);
        checkBookingStatus(booking, PENDING_STATUS);
        setBookingStatusToBooking(booking, CANCELED_STATUS);
        bookingRepository.save(booking);
        String message = NotificationConfigurator.bookingCancelled(booking);
        bookingBot.sendMessage(email, message);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateInfo(Long id, BookingRequestDto requestDto, String email) {
        Booking booking = getByIdIfUserHavePermission(id, email);
        CheckDate checkDate = checkDateMapper.toModel(requestDto.getCheckDates());
        isCheckDateValid(checkDate);
        checkBookingStatus(booking, PENDING_STATUS);
        isAccommodationFree(requestDto.getAccommodationId(), checkDate, DONT_HAVE_AVAILABLE_VALUE);
        bookingMapper.setUpdateInfoToBooking(booking, requestDto);
        bookingRepository.save(booking);
        String message = NotificationConfigurator.bookingUpdated(booking);
        bookingBot.sendMessage(email, message);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto getById(Long id, String email) {
        return bookingMapper.toDto(getByIdIfUserHavePermission(id, email));
    }

    @Override
    @Transactional
    public List<BookingDto> findAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<BookingDto> findByUserEmail(String email, Pageable pageable) {
        return bookingRepository.findAllByUser_Email(email, pageable).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    private void checkBookingStatus(Booking booking, BookingStatus.BookingStatusName statusName) {
        BookingStatus bookingStatus = booking.getStatus();
        if (!bookingStatus.getName().equals(statusName)) {
            throw new BookingInfoException("Booking expected status: " + statusName
                    + " actual status: " + bookingStatus);
        }
    }

    private Booking getByIdIfUserHavePermission(Long id, String email) {
        Booking booking = getBookingById(id);
        if (!email.equals(booking.getUser().getEmail())) {
            throw new UserDontHavePermissions("User with email: " + email
                    + " cant see booking with id: " + id);
        }
        return booking;
    }

    private void setBookingStatusToBooking(Booking booking,
                                           BookingStatus.BookingStatusName statusName) {
        BookingStatus bookingPendingStatus
                = bookingStatusRepository.findBookingStatusByName(statusName);
        booking.setStatus(bookingPendingStatus);
    }

    private void setUserToBooking(Booking booking, String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Cant find user with email: " + email));
        booking.setUser(user);
    }

    private void setAccommodationIfIsFree(Booking booking, BookingRequestDto requestDto) {
        Accommodation accommodation = getAccommodationById(requestDto.getAccommodationId());
        CheckDate checkDates = checkDateMapper.toModel(requestDto.getCheckDates());
        isAccommodationFree(requestDto.getAccommodationId(), checkDates, DONT_HAVE_AVAILABLE_VALUE);
        booking.setAccommodation(accommodation);
    }

    private void isAccommodationFree(Long id, CheckDate checkDate, Long availableValue) {
        Long available = bookingRepository.isDatesAvailableForAccommodation(
                id, checkDate.getCheckInDate(), checkDate.getCheckOutDate());

        if (available > availableValue) {
            throw new EntityAlreadyExistsException("Can't create booking where"
                    + " accommodationId: " + id
                    + " checkDates: " + checkDate);
        }
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cant find booking where id: " + id));
    }

    private Accommodation getAccommodationById(Long id) {
        return accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cant find accommodation where id: " + id));
    }

    private void isCheckDateValid(CheckDate checkDates) {
        LocalDate checkInDate = checkDates.getCheckInDate();
        LocalDate checkOutDate = checkDates.getCheckOutDate();
        LocalDate today = LocalDate.now();
        if (checkInDate.isBefore(today)
                || checkInDate.equals(checkOutDate)
                || checkInDate.isAfter(checkOutDate)) {
            throw new InvalidDateException("Invalid check in date ");
        }
    }
}
