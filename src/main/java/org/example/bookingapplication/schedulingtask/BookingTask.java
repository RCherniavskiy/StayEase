package org.example.bookingapplication.schedulingtask;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.exception.payment.StripeSessionException;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.example.bookingapplication.model.payment.Payment;
import org.example.bookingapplication.model.payment.PaymentStatus;
import org.example.bookingapplication.model.telegram.TelegramChat;
import org.example.bookingapplication.model.user.RoleType;
import org.example.bookingapplication.repository.booking.BookingRepository;
import org.example.bookingapplication.repository.bookingstatus.BookingStatusRepository;
import org.example.bookingapplication.repository.payment.PaymentRepository;
import org.example.bookingapplication.repository.paymentstatus.PaymentStatusRepository;
import org.example.bookingapplication.repository.telegramchat.TelegramChatRepository;
import org.example.bookingapplication.telegram.notification.TelegramNotificationService;
import org.example.bookingapplication.telegram.util.NotificationConfigurator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingTask {
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

    private final BookingRepository bookingRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final TelegramNotificationService telegramNotificationService;
    private final TelegramChatRepository telegramChatRepository;

    @Scheduled(cron = "0 0 10 * * *")
    public void ifCheckInDateIsToday() {
        LocalDate today = LocalDate.now();
        List<Booking> bookingList = bookingRepository.findAllByCheckInDate(today, CONFIRMED_STATUS);
        if (bookingList.isEmpty()) {
            return;
        }
        bookingList.forEach(booking -> telegramNotificationService.sendMessageAsync(
                booking.getUser().getEmail(),
                NotificationConfigurator.bookingCheckInToday(booking)));
    }

    @Scheduled(cron = "0 00 12 * * *")
    @Transactional
    public void ifCheckOutDateIsToday() {
        LocalDate today = LocalDate.now();
        System.out.println(today);
        List<Booking> bookingList
                = bookingRepository.findAllByCheckOutDate(today, CONFIRMED_STATUS);
        if (bookingList.isEmpty()) {
            getAdminsEmail().forEach(b ->
                    telegramNotificationService.sendMessageAsync(b,
                            NotificationConfigurator.nonExpiredBookingsAlert()));
            return;
        }
        updateBookingsStatus(bookingList, EXPIRED_STATUS);
        bookingList.forEach(booking -> telegramNotificationService.sendMessageAsync(
                booking.getUser().getEmail(), NotificationConfigurator.bookingExpired(booking)));
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    @Transactional
    public void ifBookingUnconfirmedOneHour() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(ONE_HOUR);
        List<Booking> bookingList
                = bookingRepository.findAllByCreatedAtAndStatus(oneHourAgo, PENDING_STATUS);
        if (bookingList.isEmpty()) {
            return;
        }
        updateBookingsStatus(bookingList, CANCELED_STATUS);
        cancelPayments(bookingList);
        bookingList.forEach(booking -> telegramNotificationService.sendMessageAsync(
                booking.getUser().getEmail(),
                NotificationConfigurator.bookingTimeExpired(booking)));
    }

    private void updateBookingsStatus(List<Booking> bookingList,
                                      BookingStatus.BookingStatusName statusName) {
        BookingStatus canceled = bookingStatusRepository.findBookingStatusByName(statusName);
        bookingList.forEach(booking -> booking.setStatus(canceled));
        bookingRepository.saveAll(bookingList);
    }

    private void cancelPayments(List<Booking> bookingList) {
        List<Long> bookingIds = bookingList.stream().map(Booking::getId).toList();
        PaymentStatus cancelStatus = paymentStatusRepository
                .findPaymentStatusByName(CANCELED_PAYMENT_STATUS);
        List<Payment> paymentList = paymentRepository
                .findAllByBookingIdInAndStatus_Name(bookingIds, PENDING_PAYMENT_STATUS);
        for (Payment payment : paymentList) {
            payment.setStatus(cancelStatus);
            try {
                Session sessionById = Session.retrieve(payment.getSessionId());
                sessionById.expire();
            } catch (StripeException e) {
                throw new StripeSessionException("Cant cancel stripe session: " + e.getMessage());
            }
        }
        paymentRepository.saveAll(paymentList);
    }

    private List<Long> getAdminsEmail() {
        List<TelegramChat> allByUserRolesName =
                telegramChatRepository.findAllByUser_Roles_Name(ADMIN_ROLE);
        return allByUserRolesName.stream().map(TelegramChat::getChatId).toList();
    }
}
