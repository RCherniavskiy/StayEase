package org.example.bookingapplication.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.payment.responce.PaymentDto;
import org.example.bookingapplication.dto.payment.responce.PaymentInfoDto;
import org.example.bookingapplication.exception.payment.CantPaidBookingException;
import org.example.bookingapplication.exception.payment.PaymentCancelException;
import org.example.bookingapplication.exception.payment.PaymentDontConfirmException;
import org.example.bookingapplication.exception.payment.StripeSessionException;
import org.example.bookingapplication.exception.repository.EntityNotFoundException;
import org.example.bookingapplication.exception.user.UserDontHavePermissions;
import org.example.bookingapplication.mapper.BookingMapper;
import org.example.bookingapplication.mapper.PaymentMapper;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.example.bookingapplication.model.booking.CheckDate;
import org.example.bookingapplication.model.payment.Payment;
import org.example.bookingapplication.model.payment.PaymentStatus;
import org.example.bookingapplication.repository.booking.BookingRepository;
import org.example.bookingapplication.repository.bookingstatus.BookingStatusRepository;
import org.example.bookingapplication.repository.payment.PaymentRepository;
import org.example.bookingapplication.repository.paymentstatus.PaymentStatusRepository;
import org.example.bookingapplication.service.PaymentService;
import org.example.bookingapplication.telegram.notification.TelegramNotificationService;
import org.example.bookingapplication.telegram.util.NotificationConfigurator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private static final SessionCreateParams.PaymentMethodType PAYMENT_METHOD
            = SessionCreateParams.PaymentMethodType.CARD;
    private static final SessionCreateParams.Mode SESSION_MODE
            = SessionCreateParams.Mode.PAYMENT;
    private static final PaymentStatus.PaymentStatusName PENDING_PAYMENT_STATUS
            = PaymentStatus.PaymentStatusName.PENDING;
    private static final PaymentStatus.PaymentStatusName PAID_PAYMENT_STATUS
            = PaymentStatus.PaymentStatusName.PAID;
    private static final PaymentStatus.PaymentStatusName CANCEL_PAYMENT_STATUS
            = PaymentStatus.PaymentStatusName.CANCEL;
    private static final BookingStatus.BookingStatusName PENDING_BOOKING_STATUS
            = BookingStatus.BookingStatusName.PENDING;
    private static final BookingStatus.BookingStatusName CONFIRMED_BOOKING_STATUS
            = BookingStatus.BookingStatusName.CONFIRMED;
    private static final BookingStatus.BookingStatusName CANCELED_BOOKING_STATUS
            = BookingStatus.BookingStatusName.CANCELED;
    private static final String STRIPE_PAID_STATUS = "paid";
    private static final String CURRENCY = "USD";
    private static final String PRODUCT_NAME = "Housing reservation";
    private final String successUrl;
    private final String cancelUrl;
    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentMapper paymentMapper;
    private final BookingRepository bookingRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final BookingMapper bookingMapper;
    private final TelegramNotificationService telegramNotificationService;

    @Override
    public String createPaymentCheckoutSession(Long id, String email) {
        Booking booking = getBookingById(id);
        checkBookingStatus(booking);
        checkBookingUser(email, booking);
        Optional<String> paymentUrlOptional = getUrlIfPaymentCreate(id);
        if (paymentUrlOptional.isPresent()) {
            return paymentUrlOptional.get();
        }
        SessionCreateParams sessionParams = getSessionParams(booking);
        Session session = getStripeSession(sessionParams);
        Payment payment = createPayment(session, booking);
        paymentRepository.save(payment);
        String message = NotificationConfigurator.paymentInitiated(payment);
        telegramNotificationService.sendMessageAsync(email, message);
        return session.getUrl();
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cant find booking with id: " + id));
    }

    private void checkBookingStatus(Booking booking) {
        if (!booking.getStatus().getName().equals(PENDING_BOOKING_STATUS)) {
            throw new CantPaidBookingException("Cant paid booking with id: " + booking.getId()
                    + " because booking status: " + booking.getStatus().getName());
        }
    }

    private void checkBookingUser(String email, Booking booking) {
        if (!email.equals(booking.getUser().getEmail())) {
            throw new UserDontHavePermissions("User with email: " + email
                    + " cant have permission to booking with id: " + booking.getId());
        }
    }

    private Optional<String> getUrlIfPaymentCreate(Long bookingId) {
        String url = null;
        Optional<Payment> optional = paymentRepository.findPaymentByBookingId(bookingId);
        if (optional.isPresent()) {
            Payment payment = optional.get();
            if (payment.getStatus().getName().equals(PENDING_PAYMENT_STATUS)) {
                url = payment.getSessionUrl();
            } else {
                throw new CantPaidBookingException("Can't pay booking with id: " + bookingId);
            }
        }
        return Optional.ofNullable(url);
    }

    private SessionCreateParams getSessionParams(Booking booking) {
        return new SessionCreateParams.Builder()
                .addPaymentMethodType(PAYMENT_METHOD)
                .addLineItem(getLineItem(booking))
                .setMode(SESSION_MODE)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .build();
    }

    private SessionCreateParams.LineItem getLineItem(Booking booking) {
        return new SessionCreateParams.LineItem.Builder()
                .setPriceData(new SessionCreateParams.LineItem.PriceData.Builder()
                        .setCurrency(CURRENCY)
                        .setUnitAmount(getAmount(booking))
                        .setProductData(new SessionCreateParams
                                .LineItem.PriceData.ProductData.Builder()
                                .setName(PRODUCT_NAME)
                                .setDescription(bookingMapper.getBookingDescription(booking))
                                .build())
                        .build())
                .setQuantity(getQuantity(booking.getCheckDates()))
                .build();
    }

    private long getAmount(Booking booking) {
        BigDecimal dailyRate = booking.getAccommodation().getDailyRate();
        return dailyRate.multiply(BigDecimal.valueOf(100L)).longValue();
    }

    private long getQuantity(CheckDate checkDate) {
        LocalDate checkInDate = checkDate.getCheckInDate();
        LocalDate checkOutDate = checkDate.getCheckOutDate();
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    private Session getStripeSession(SessionCreateParams sessionParams) {
        try {
            return Session.create(sessionParams);
        } catch (StripeException e) {
            throw new StripeSessionException(
                    "Cant initialize Stripe payment session " + e.getMessage());
        }
    }

    private Payment createPayment(Session session, Booking booking) {
        Payment newPayment = new Payment();
        newPayment.setSessionUrl(session.getUrl());
        newPayment.setSessionId(session.getId());
        newPayment.setBooking(booking);
        newPayment.setAmount(BigDecimal.valueOf(session.getAmountTotal()));
        setStatusToPayment(newPayment, PENDING_PAYMENT_STATUS);
        return newPayment;
    }

    private void setStatusToPayment(Payment payment, PaymentStatus.PaymentStatusName statusName) {
        PaymentStatus paidStatus = paymentStatusRepository.findPaymentStatusByName(statusName);
        payment.setStatus(paidStatus);
    }

    @Override
    public PaymentDto successPayment(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);
        if (payment.getStatus().getName().equals(PAID_PAYMENT_STATUS)) {
            return paymentMapper.toDto(payment);
        }
        Session session = getSessionById(payment.getSessionId());
        checkSessionPaymentStatus(session.getPaymentStatus());
        updateBookingStatus(payment.getBooking(), CONFIRMED_BOOKING_STATUS);
        updatePaymentStatus(payment, PAID_PAYMENT_STATUS);
        String paymentMessage = NotificationConfigurator.paymentSuccessful(payment);
        String bookingMessage = NotificationConfigurator.bookingConfirmed(payment.getBooking());
        String email = payment.getBooking().getUser().getEmail();
        telegramNotificationService.sendMessageAsync(email, paymentMessage);
        telegramNotificationService.sendMessageAsync(email, bookingMessage);
        return paymentMapper.toDto(payment);
    }

    private Payment getPaymentBySessionId(String sessionId) {
        return paymentRepository.findPaymentBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Cant find payment where session id: "
                        + sessionId));
    }

    private Session getSessionById(String id) {
        try {
            return Session.retrieve(id);
        } catch (StripeException e) {
            throw new StripeSessionException("Problem with get stripe session: " + e.getMessage());
        }
    }

    private void checkSessionPaymentStatus(String paymentStatus) {
        if (!STRIPE_PAID_STATUS.equals(paymentStatus)) {
            throw new PaymentDontConfirmException("Current payment status is " + paymentStatus);
        }
    }

    private void updateBookingStatus(Booking booking, BookingStatus.BookingStatusName statusName) {
        BookingStatus bookingStatus
                = bookingStatusRepository.findBookingStatusByName(statusName);
        booking.setStatus(bookingStatus);
        bookingRepository.save(booking);
    }

    private void updatePaymentStatus(Payment payment, PaymentStatus.PaymentStatusName statusName) {
        setStatusToPayment(payment, statusName);
        paymentRepository.save(payment);
    }

    @Override
    public PaymentDto cancelPaymentAndBooking(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);
        checkPaymentStatus(payment, PENDING_PAYMENT_STATUS);
        updatePaymentStatus(payment, CANCEL_PAYMENT_STATUS);
        updateBookingStatus(payment.getBooking(),CANCELED_BOOKING_STATUS);
        Session sessionById = getSessionById(payment.getSessionId());
        try {
            sessionById.expire();
        } catch (StripeException e) {
            throw new StripeSessionException("Cant cancel stripe session: " + e.getMessage());
        }
        String paymentMessage = NotificationConfigurator.paymentCancelled(payment);
        String bookingMessage = NotificationConfigurator.bookingCancelled(payment.getBooking());
        String email = payment.getBooking().getUser().getEmail();
        telegramNotificationService.sendMessageAsync(email, paymentMessage);
        telegramNotificationService.sendMessageAsync(email, bookingMessage);
        return paymentMapper.toDto(payment);
    }

    private void checkPaymentStatus(Payment payment, PaymentStatus.PaymentStatusName statusName) {
        if (!payment.getStatus().getName().equals(statusName)) {
            throw new PaymentCancelException("Cant cancel payment with id: " + payment.getId()
                    + " because status: " + payment.getStatus().getName());
        }
    }

    @Override
    public List<PaymentInfoDto> findAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toInfoDto)
                .toList();
    }

    @Override
    public List<PaymentInfoDto> findPaymentsByUserEmail(String email) {
        return paymentRepository.findPaymentByBookingUserEmail(email).stream()
                .map(paymentMapper::toInfoDto)
                .toList();
    }
}
