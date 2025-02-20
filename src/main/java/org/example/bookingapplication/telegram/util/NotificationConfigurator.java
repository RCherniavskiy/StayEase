package org.example.bookingapplication.telegram.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.example.bookingapplication.model.accommodation.Address;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.payment.Payment;
import org.springframework.stereotype.Component;

@Component
public class NotificationConfigurator {
    private static final String NON_EXPIRED_BOOKINGS_TEMPLATE = "No expired bookings today!";
    private static final String TOKEN_NOT_VALID_TEMPLATE = "Hello! You have dont valid auth token!";
    private static final String BOOKING_STARTED_TEMPLATE = "Hello %s! You have started a "
            + "booking process!";
    private static final String BOOKING_CREATED_TEMPLATE = "Hi %s! Your booking for %s "
            + "at %s from %s to %s has been successfully created.";
    private static final String BOOKING_UPDATED_TEMPLATE = "Hi %s! Your booking for %s "
            + "at %s from %s to %s has been successfully updated.";
    private static final String BOOKING_CONFIRMED_TEMPLATE = "Dear %s, Your booking for %s "
            + "at %s from %s to %s has been confirmed. We're looking forward to hosting you!";
    private static final String BOOKING_CANCELLED_TEMPLATE = "Hi %s, Your booking for %s "
            + "at %s from %s to %s has been canceled.";
    private static final String BOOKING_TIME_EXPIRED_TEMPLATE = "Hi %s, Your booking for %s "
            + "at %s from %s to %s has been canceled because time out";
    private static final String BOOKING_EXPIRED_TEMPLATE = "Dear %s, Your booking for %s "
            + "at %s from %s to %s has expired. Please consider rebooking if needed.";
    private static final String BOOKING_CHECK_IN_TEMPLATE = "Dear %s, Your booking for %s "
            + "at %s from %s to %s wait for you at 12:00.";
    private static final String PAYMENT_INITIATED_TEMPLATE = "Hello %s! Your payment for "
            + "booking ID: %s has been initiated. Your url for pay: %s.";
    private static final String PAYMENT_SUCCESSFUL_TEMPLATE = "Congratulations %s!"
            + " Your payment for booking ID: %s has been successfully processed.";
    private static final String PAYMENT_CANCELLED_TEMPLATE = "Hi %s, Your payment for "
            + "booking ID: %s has been canceled.";
    private static final String NEW_BOOKING_CONFIRMATION_REQUIRED_TEMPLATE = "Hi Admin, There is a"
            + " new booking pending confirmation for %s at %s. Please review and confirm.";
    private static final String EXPIRED_BOOKINGS_ALERT_TEMPLATE = "Attention Admin, There are "
            + "expired bookings that need attention. Please review and take the necessary actions.";

    public static String dontValidToken() {
        return TOKEN_NOT_VALID_TEMPLATE;
    }

    public static String bookingStarted(String userName) {
        return String.format(BOOKING_STARTED_TEMPLATE, userName);
    }

    public static String bookingCreated(Booking booking) {
        return getString(booking, BOOKING_CREATED_TEMPLATE);
    }

    public static String bookingUpdated(Booking booking) {
        return getString(booking, BOOKING_UPDATED_TEMPLATE);
    }

    public static String bookingConfirmed(Booking booking) {
        return getString(booking, BOOKING_CONFIRMED_TEMPLATE);
    }

    public static String bookingCancelled(Booking booking) {
        return getString(booking, BOOKING_CANCELLED_TEMPLATE);
    }

    public static String bookingTimeExpired(Booking booking) {
        return getString(booking, BOOKING_TIME_EXPIRED_TEMPLATE);
    }

    public static String bookingCheckInToday(Booking booking) {
        return getString(booking, BOOKING_CHECK_IN_TEMPLATE);
    }

    private static String getString(Booking booking, String bookingTimeExpiredTemplate) {
        String firstName = booking.getUser().getFirstName();
        String typeName = booking.getAccommodation().getType().getName().toString();
        LocalDate checkInDate = booking.getCheckDates().getCheckInDate();
        LocalDate checkOutDate = booking.getCheckDates().getCheckOutDate();
        String address = formatAddress(booking.getAccommodation().getAddress());
        return String.format(
                bookingTimeExpiredTemplate, firstName, typeName,
                address, formatDate(checkInDate), formatDate(checkOutDate));
    }

    public static String bookingExpired(Booking booking) {
        return getString(booking, BOOKING_EXPIRED_TEMPLATE);
    }

    public static String paymentInitiated(Payment payment) {
        Booking booking = payment.getBooking();
        String firstName = booking.getUser().getFirstName();
        Long bookingId = booking.getId();
        String sessionUrl = payment.getSessionUrl();
        return String.format(PAYMENT_INITIATED_TEMPLATE, firstName, bookingId, sessionUrl);
    }

    public static String paymentSuccessful(Payment payment) {
        Booking booking = payment.getBooking();
        String firstName = booking.getUser().getFirstName();
        Long bookingId = booking.getId();
        return String.format(PAYMENT_SUCCESSFUL_TEMPLATE, firstName, bookingId);
    }

    public static String paymentCancelled(Payment payment) {
        Booking booking = payment.getBooking();
        String firstName = booking.getUser().getFirstName();
        Long bookingId = booking.getId();
        return String.format(PAYMENT_CANCELLED_TEMPLATE, firstName, bookingId);
    }

    public static String nonExpiredBookingsAlert() {
        return NON_EXPIRED_BOOKINGS_TEMPLATE;
    }

    private static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private static String formatAddress(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append(address.getCountry()).append(", ")
                .append(address.getCity()).append(", ")
                .append("Street ").append(address.getStreet()).append(", ")
                .append("House ").append(address.getHouseNumber());
        if (address.getApartmentNumber() != null && !address.getApartmentNumber().isEmpty()) {
            sb.append(", Apartment ").append(address.getApartmentNumber());
        }
        return sb.toString();
    }
}
