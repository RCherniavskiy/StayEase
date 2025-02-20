package org.example.bookingapplication.mapper;

import org.example.bookingapplication.config.MapperConfig;
import org.example.bookingapplication.dto.payment.responce.PaymentDto;
import org.example.bookingapplication.dto.payment.responce.PaymentInfoDto;
import org.example.bookingapplication.model.payment.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {BookingMapper.class, PaymentStatusMapper.class})
public interface PaymentMapper {
    PaymentDto toDto(Payment payment);

    @Mapping(target = "bookingId", source = "booking.id")
    PaymentInfoDto toInfoDto(Payment payment);
}
