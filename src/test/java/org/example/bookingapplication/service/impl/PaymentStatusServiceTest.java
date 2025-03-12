package org.example.bookingapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.example.bookingapplication.dto.paymentstatuses.response.PaymentStatusDto;
import org.example.bookingapplication.mapper.PaymentStatusMapper;
import org.example.bookingapplication.model.payment.PaymentStatus;
import org.example.bookingapplication.repository.paymentstatus.PaymentStatusRepository;
import org.example.bookingapplication.testutil.PaymentStatusSampleUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentStatusServiceTest {
    @Mock
    private PaymentStatusRepository paymentStatusRepository;
    @Mock
    private PaymentStatusMapper paymentStatusMapper;
    @InjectMocks
    private PaymentStatusServiceImpl paymentStatusService;

    @Test
    @DisplayName("Find all payment status with exist data")
    void findAll_findExistData_ReturnTwoObjects() {
        // Given: Two payment statuses in the database
        PaymentStatus paymentStatus1 = PaymentStatusSampleUtil
                .createSamplePaymentStatus(1L, PaymentStatus.PaymentStatusName.PAID);
        PaymentStatus paymentStatus2 = PaymentStatusSampleUtil
                .createSamplePaymentStatus(2L, PaymentStatus.PaymentStatusName.PENDING);

        when(paymentStatusRepository.findAll())
                .thenReturn(List.of(paymentStatus1, paymentStatus2));

        // Mapping the statuses to DTOs
        PaymentStatusDto paymentStatusDto1 = PaymentStatusSampleUtil
                .createSamplePaymentStatusDto(1L, PaymentStatus.PaymentStatusName.PAID);
        when(paymentStatusMapper.toDto(paymentStatus1)).thenReturn(paymentStatusDto1);
        PaymentStatusDto paymentStatusDto2 = PaymentStatusSampleUtil
                .createSamplePaymentStatusDto(2L, PaymentStatus.PaymentStatusName.PENDING);
        when(paymentStatusMapper.toDto(paymentStatus2)).thenReturn(paymentStatusDto2);

        // When: Retrieving all payment statuses
        List<PaymentStatusDto> result = paymentStatusService.findAll();

        // Then: Verify the result contains the correct number
        // of elements and matches the expected data
        assertEquals(2, result.size());
        assertEquals(paymentStatusDto1, result.get(0));
        assertEquals(paymentStatusDto2, result.get(1));

        // Verify interactions with mocks
        verify(paymentStatusRepository, times(1)).findAll();
        verify(paymentStatusMapper, times(1)).toDto(paymentStatus1);
        verify(paymentStatusMapper, times(1)).toDto(paymentStatus2);
        verifyNoMoreInteractions(paymentStatusRepository);
        verifyNoMoreInteractions(paymentStatusMapper);
    }
}
