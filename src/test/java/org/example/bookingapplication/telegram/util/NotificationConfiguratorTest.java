package org.example.bookingapplication.telegram.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.model.accommodation.AccommodationType;
import org.example.bookingapplication.model.accommodation.Address;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.booking.CheckDate;
import org.example.bookingapplication.model.payment.Payment;
import org.example.bookingapplication.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationConfiguratorTest {
    @InjectMocks
    private NotificationConfigurator notificationConfigurator;
    private User user;
    private Address address;
    private Accommodation accommodation;
    private CheckDate checkDates;
    private Booking booking;
    private Payment payment;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setFirstName("John");

        address = new Address();
        address.setCountry("USA");
        address.setCity("New York");
        address.setStreet("5th Avenue");
        address.setHouseNumber("101");
        address.setApartmentNumber("3B");

        AccommodationType type = new AccommodationType();
        type.setName(AccommodationType.AccommodationTypeName.APARTMENT);

        accommodation = new Accommodation();
        accommodation.setAddress(address);
        accommodation.setType(type);

        checkDates = new CheckDate();
        checkDates.setCheckInDate(LocalDate.of(2023, 12, 1));
        checkDates.setCheckOutDate(LocalDate.of(2023, 12, 10));

        booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setAccommodation(accommodation);
        booking.setCheckDates(checkDates);

        payment = new Payment();
        payment.setBooking(booking);
        payment.setSessionUrl("http://payment.com/session/12345");
    }

    @Test
    @DisplayName("Test Invalid Token Message")
    public void testDontValidToken() {
        String expected = "Hello! You have dont valid auth token!";
        String actual = notificationConfigurator.dontValidToken();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test Booking Started Notification")
    public void testBookingStarted() {
        String expected = "Hello John! You have started a booking process!";
        String actual = notificationConfigurator.bookingStarted(user.getFirstName());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test Booking Created Notification")
    public void testBookingCreated() {
        String expected = "Hi John! Your booking for APARTMENT at USA, New York, "
                + "Street 5th Avenue, House 101, Apartment 3B from 01.12.2023 to 10.12.2023 "
                + "has been successfully created.";
        String actual = notificationConfigurator.bookingCreated(booking);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test Booking Updated Notification")
    public void testBookingUpdated() {
        String expected = "Hi John! Your booking for APARTMENT at USA, New York, "
                + "Street 5th Avenue, House 101, Apartment 3B from 01.12.2023 to 10.12.2023 "
                + "has been successfully updated.";
        String actual = notificationConfigurator.bookingUpdated(booking);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test Booking Confirmed Notification")
    public void testBookingConfirmed() {
        String expected = "Dear John, Your booking for APARTMENT at USA, New York, "
                + "Street 5th Avenue, House 101, Apartment 3B from 01.12.2023 to 10.12.2023 "
                + "has been confirmed. We're looking forward to hosting you!";
        String actual = notificationConfigurator.bookingConfirmed(booking);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test Booking Cancelled Notification")
    public void testBookingCancelled() {
        String expected = "Hi John, Your booking for APARTMENT at USA, New York, Street "
                + "5th Avenue, House 101, Apartment 3B from 01.12.2023 to 10.12.2023 "
                + "has been canceled.";
        String actual = notificationConfigurator.bookingCancelled(booking);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test Booking Check-In Notification")
    public void testBookingCheckInToday() {
        String expected = "Dear John, Your booking for APARTMENT at USA, New York, "
                + "Street 5th Avenue, House 101, Apartment 3B from 01.12.2023 to 10.12.2023"
                + " wait for you at 12:00.";
        String actual = notificationConfigurator.bookingCheckInToday(booking);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test Payment Initiated Notification")
    public void testPaymentInitiated() {
        String expected = "Hello John! Your payment for booking ID: 1 has been initiated. "
                + "Your url for pay: http://payment.com/session/12345.";
        String actual = notificationConfigurator.paymentInitiated(payment);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test Payment Successful Notification")
    public void testPaymentSuccessful() {
        String expected = "Congratulations John! Your payment for booking ID: 1 has been "
                + "successfully processed.";
        String actual = notificationConfigurator.paymentSuccessful(payment);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test Payment Cancelled Notification")
    public void testPaymentCancelled() {
        String expected = "Hi John, Your payment for booking ID: 1 has been canceled.";
        String actual = notificationConfigurator.paymentCancelled(payment);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test Booking Time Expired Notification")
    public void testBookingTimeExpired() {
        String expected = String.format(
                "Hi John, Your booking for APARTMENT at USA, New York, Street 5th Avenue, "
                        + "House 101, Apartment 3B from 01.12.2023 to 10.12.2023 has been canceled "
                        + "because time out"
        );
        String actual = notificationConfigurator.bookingTimeExpired(booking);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test Booking Expired Notification")
    public void testBookingExpired() {
        String expected = String.format(
                "Dear John, Your booking for APARTMENT at USA, New York, Street 5th Avenue, "
                        + "House 101, Apartment 3B from 01.12.2023 to 10.12.2023 has expired. "
                        + "Please consider rebooking if needed."
        );
        String actual = notificationConfigurator.bookingExpired(booking);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test Non-Expired Bookings Alert")
    public void testNonExpiredBookingsAlert() {
        String expected = "No expired bookings today!";
        String actual = notificationConfigurator.nonExpiredBookingsAlert();
        assertEquals(expected, actual);
    }
}
