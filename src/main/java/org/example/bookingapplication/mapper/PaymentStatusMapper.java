package org.example.bookingapplication.mapper;

import org.example.bookingapplication.config.MapperConfig;
import org.example.bookingapplication.dto.paymentstatuses.response.PaymentStatusDto;
import org.example.bookingapplication.model.payment.PaymentStatus;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentStatusMapper {
    PaymentStatusDto toDto(PaymentStatus paymentStatus);
}
