package org.example.bookingapplication.schedulingtask;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.example.bookingapplication.model.payment.PaymentStatus;
import org.example.bookingapplication.model.user.RoleType;
import org.example.bookingapplication.repository.booking.BookingRepository;
import org.example.bookingapplication.repository.bookingstatus.BookingStatusRepository;
import org.example.bookingapplication.repository.payment.PaymentRepository;
import org.example.bookingapplication.repository.paymentstatus.PaymentStatusRepository;
import org.example.bookingapplication.repository.telegramchat.TelegramChatRepository;
import org.example.bookingapplication.telegram.notification.TelegramNotificationService;
import org.example.bookingapplication.telegram.util.NotificationConfigurator;
import org.example.bookingapplication.testutil.BookingSampleUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingTaskTest {
    private static final Long ONE_HOUR = 1L;
    private static final BookingStatus.BookingStatusName PENDING_STATUS
            = BookingStatus.BookingStatusName.PENDING;
    private static final BookingStatus.BookingStatusName CONFIRMED_STATUS
            = BookingStatus.BookingStatusName.CONFIRMED;
    private static final BookingStatus.BookingStatusName EXPIRED_STATUS
            = BookingStatus.BookingStatusName.EXPIRED;

    private static final BookingStatus.BookingStatusName CANCELED_STATUS
            = BookingStatus.BookingStatusName.CANCELED;

    private static final PaymentStatus.PaymentStatusName CANCELED_PAYMENT_STATUS
            = PaymentStatus.PaymentStatusName.CANCEL;
    private static final PaymentStatus.PaymentStatusName PENDING_PAYMENT_STATUS
            = PaymentStatus.PaymentStatusName.PENDING;
    private static final RoleType.RoleName ADMIN_ROLE = RoleType.RoleName.ADMIN;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingStatusRepository bookingStatusRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentStatusRepository paymentStatusRepository;
    @Mock
    private TelegramNotificationService telegramNotificationService;
    @Mock
    private TelegramChatRepository telegramChatRepository;
    @Mock
    private NotificationConfigurator notificationConfigurator;
    @InjectMocks
    private BookingTask bookingTask;

    @Test
    @DisplayName("Send massage to user if check in day is today")
    void ifCheckInDateIsToday_sendMassageToTelegramWithBookings_SendMassages() {
        Booking sampleBooking1 = BookingSampleUtil.createSampleBooking(1L);
        sampleBooking1.getCheckDates().setCheckInDate(LocalDate.now());
        sampleBooking1.getStatus().setName(CONFIRMED_STATUS);
        Booking sampleBooking2 = BookingSampleUtil.createSampleBooking(2L);
        sampleBooking2.getCheckDates().setCheckInDate(LocalDate.now());
        sampleBooking2.getStatus().setName(CONFIRMED_STATUS);

        when(bookingRepository.findAllByCheckInDate(LocalDate.now(), CONFIRMED_STATUS))
                .thenReturn(List.of(sampleBooking1, sampleBooking2));

        bookingTask.ifCheckInDateIsToday();

        verify(bookingRepository, times(1)).findAllByCheckInDate(
                LocalDate.now(), CONFIRMED_STATUS);
        verify(telegramNotificationService, times(1)).sendMessageAsync(
                sampleBooking1.getUser().getEmail(),
                NotificationConfigurator.bookingCheckInToday(sampleBooking1));
        verify(telegramNotificationService, times(1)).sendMessageAsync(
                sampleBooking2.getUser().getEmail(),
                NotificationConfigurator.bookingCheckInToday(sampleBooking2));
        verifyNoMoreInteractions(bookingRepository, telegramNotificationService);
    }

    @Test
    @DisplayName("Dont send massage if bookings is non exist")
    void ifCheckInDateIsToday_sendMassageToTelegramWithOutBookings_Nothing() {
        when(bookingRepository.findAllByCheckInDate(LocalDate.now(), CONFIRMED_STATUS))
                .thenReturn(List.of());

        bookingTask.ifCheckInDateIsToday();

        verify(bookingRepository, times(1)).findAllByCheckInDate(LocalDate.now(), CONFIRMED_STATUS);
        verifyNoMoreInteractions(bookingRepository, telegramNotificationService);
    }
}
