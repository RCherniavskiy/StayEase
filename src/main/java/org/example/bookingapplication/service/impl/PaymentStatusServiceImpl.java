package org.example.bookingapplication.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.paymentstatuses.response.PaymentStatusDto;
import org.example.bookingapplication.mapper.PaymentStatusMapper;
import org.example.bookingapplication.model.payment.PaymentStatus;
import org.example.bookingapplication.repository.paymentstatus.PaymentStatusRepository;
import org.example.bookingapplication.service.PaymentStatusService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentStatusServiceImpl implements PaymentStatusService {
    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentStatusMapper paymentStatusMapper;

    @Override
    public List<PaymentStatusDto> findAll() {
        List<PaymentStatus> listOfTypes = paymentStatusRepository.findAll();
        return listOfTypes.stream()
                .map(paymentStatusMapper::toDto)
                .toList();
    }
}
