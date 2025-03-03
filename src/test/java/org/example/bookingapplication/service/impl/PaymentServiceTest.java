package org.example.bookingapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.example.bookingapplication.dto.payment.responce.PaymentDto;
import org.example.bookingapplication.dto.payment.responce.PaymentInfoDto;
import org.example.bookingapplication.exception.payment.CantPaidBookingException;
import org.example.bookingapplication.exception.repository.EntityNotFoundException;
import org.example.bookingapplication.exception.user.UserDontHavePermissions;
import org.example.bookingapplication.mapper.PaymentMapper;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.example.bookingapplication.model.payment.Payment;
import org.example.bookingapplication.model.payment.PaymentStatus;
import org.example.bookingapplication.model.user.User;
import org.example.bookingapplication.repository.booking.BookingRepository;
import org.example.bookingapplication.repository.payment.PaymentRepository;
import org.example.bookingapplication.testutil.BookingSampleUtil;
import org.example.bookingapplication.testutil.PaymentSampleUtil;
import org.example.bookingapplication.testutil.UserSampleUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Create checkout session with not valid booking id")
    void createPaymentCheckoutSession_createPaymentSessionWithNotValidId_throwException() {
        User sampleUser = UserSampleUtil.createSampleUser(1L);

        when(bookingRepository.findById(-1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> paymentService.createPaymentCheckoutSession(-1L, sampleUser.getEmail()));

        verify(bookingRepository, times(1)).findById(-1L);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Create checkout session with not valid booking status")
    void createPaymentCheckoutSession_createPaymentSessionWithNotValidStatus_throwException() {
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        Booking sampleBooking = BookingSampleUtil.createSampleBooking(1L);
        sampleBooking.getStatus().setName(BookingStatus.BookingStatusName.CANCELED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(sampleBooking));

        assertThrows(CantPaidBookingException.class,
                () -> paymentService.createPaymentCheckoutSession(1L, sampleUser.getEmail()));

        verify(bookingRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Create checkout session with not valid user")
    void createPaymentCheckoutSession_createPaymentSessionWithNotValidUser_throwException() {
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        sampleUser.setEmail("not valid email");
        Booking sampleBooking = BookingSampleUtil.createSampleBooking(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(sampleBooking));

        assertThrows(UserDontHavePermissions.class,
                () -> paymentService.createPaymentCheckoutSession(1L, sampleUser.getEmail()));

        verify(bookingRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Create checkout session with not valid payment status")
    void createPaymentCheckoutSession_createWithNotValidPaymentStatus_throwException() {
        Payment samplePayment = PaymentSampleUtil.createSamplePayment(1L);
        samplePayment.getStatus().setName(PaymentStatus.PaymentStatusName.CANCEL);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(samplePayment.getBooking()));
        when(paymentRepository.findPaymentByBookingId(1L)).thenReturn(Optional.of(samplePayment));

        assertThrows(CantPaidBookingException.class,
                () -> paymentService.createPaymentCheckoutSession(
                        1L, samplePayment.getBooking().getUser().getEmail()));

        verify(bookingRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).findPaymentByBookingId(1L);
        verifyNoMoreInteractions(bookingRepository, paymentRepository);
    }

    @Test
    @DisplayName("Create checkout session with exist payment")
    void createPaymentCheckoutSession_createPaymentSessionWithExistValidPayment_returnUrl() {
        Payment samplePayment = PaymentSampleUtil.createSamplePayment(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(samplePayment.getBooking()));
        when(paymentRepository.findPaymentByBookingId(1L)).thenReturn(Optional.of(samplePayment));

        String result = paymentService.createPaymentCheckoutSession(
                1L, samplePayment.getBooking().getUser().getEmail());

        assertNotNull(result);
        assertEquals(samplePayment.getSessionUrl(), result);

        verify(bookingRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).findPaymentByBookingId(1L);
        verifyNoMoreInteractions(bookingRepository, paymentRepository);
    }

    @Test
    @DisplayName("Success successes payment")
    void successPayment_successSuccessPayment_returnPaidPayment() {
        Payment samplePayment = PaymentSampleUtil.createSamplePayment(1L);
        samplePayment.getStatus().setName(PaymentStatus.PaymentStatusName.PAID);
        PaymentDto samplePaymentDto = PaymentSampleUtil.createSamplePaymentDto(1L);
        samplePaymentDto.getStatus().setName(PaymentStatus.PaymentStatusName.PAID.name());

        when(paymentRepository.findPaymentBySessionId(samplePayment.getSessionId()))
                .thenReturn(Optional.of(samplePayment));
        when(paymentMapper.toDto(samplePayment)).thenReturn(samplePaymentDto);
        PaymentDto result = paymentService.successPayment(samplePayment.getSessionId());

        assertNotNull(result);
        assertEquals(samplePaymentDto, result);

        verify(paymentRepository, times(1)).findPaymentBySessionId(samplePayment.getSessionId());
        verify(paymentMapper, times(1)).toDto(samplePayment);
        verifyNoMoreInteractions(bookingRepository, paymentRepository, paymentMapper);
    }

    @Test
    @DisplayName("Success payment with not valid session id")
    void successPayment_successPaymentWithNotValidSessionId_ThrowException() {
        String nonValidSessionId = "nonValidSessionId";

        when(paymentRepository.findPaymentBySessionId(nonValidSessionId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> paymentService.successPayment(nonValidSessionId));

        verify(paymentRepository, times(1)).findPaymentBySessionId(nonValidSessionId);
        verifyNoMoreInteractions(bookingRepository, paymentRepository, paymentMapper);
    }

    @Test
    @DisplayName("Find all payment with valid data")
    void findAllPayments_findAllPaymentsWithValidData_returnPaymentsList() {
        Payment samplePayment1 = PaymentSampleUtil.createSamplePayment(1L);
        Payment samplePayment2 = PaymentSampleUtil.createSamplePayment(2L);
        PaymentInfoDto samplePaymentDto1 = PaymentSampleUtil.createSamplePaymentInfoDto(1L);
        PaymentInfoDto samplePaymentDto2 = PaymentSampleUtil.createSamplePaymentInfoDto(2L);

        when(paymentRepository.findAll()).thenReturn(List.of(samplePayment1, samplePayment2));
        when(paymentMapper.toInfoDto(samplePayment1)).thenReturn(samplePaymentDto1);
        when(paymentMapper.toInfoDto(samplePayment2)).thenReturn(samplePaymentDto2);

        List<PaymentInfoDto> result = paymentService.findAllPayments();

        assertEquals(2, result.size());
        assertEquals(List.of(samplePaymentDto1, samplePaymentDto2), result);

        verify(paymentRepository, times(1)).findAll();
        verify(paymentMapper, times(1)).toInfoDto(samplePayment1);
        verify(paymentMapper, times(1)).toInfoDto(samplePayment2);
        verifyNoMoreInteractions(paymentRepository, paymentMapper);
    }

    @Test
    @DisplayName("Find all payment by user email with valid data")
    void findPaymentsByUserEmail_findUserPaymentsWithValidData_returnPaymentsList() {
        Payment samplePayment1 = PaymentSampleUtil.createSamplePayment(1L);
        Payment samplePayment2 = PaymentSampleUtil.createSamplePayment(2L);
        PaymentInfoDto samplePaymentDto1 = PaymentSampleUtil.createSamplePaymentInfoDto(1L);
        PaymentInfoDto samplePaymentDto2 = PaymentSampleUtil.createSamplePaymentInfoDto(2L);
        String email = samplePayment1.getBooking().getUser().getEmail();

        when(paymentRepository.findPaymentByBookingUserEmail(email))
                .thenReturn(List.of(samplePayment1, samplePayment2));
        when(paymentMapper.toInfoDto(samplePayment1)).thenReturn(samplePaymentDto1);
        when(paymentMapper.toInfoDto(samplePayment2)).thenReturn(samplePaymentDto2);

        List<PaymentInfoDto> result = paymentService.findPaymentsByUserEmail(email);

        assertEquals(2, result.size());
        assertEquals(List.of(samplePaymentDto1, samplePaymentDto2), result);

        verify(paymentRepository, times(1)).findPaymentByBookingUserEmail(email);
        verify(paymentMapper, times(1)).toInfoDto(samplePayment1);
        verify(paymentMapper, times(1)).toInfoDto(samplePayment2);
        verifyNoMoreInteractions(paymentRepository, paymentMapper);
    }
}
