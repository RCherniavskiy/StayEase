package org.example.bookingapplication.telegram.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.example.bookingapplication.model.accommodation.Address;
import org.example.bookingapplication.model.booking.Booking;
import org.springframework.stereotype.Component;

@Component
public class NotificationConfigurator {
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

    public static String bookingCancelled(Booking booking) {
        return getString(booking, BOOKING_CANCELLED_TEMPLATE);
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
