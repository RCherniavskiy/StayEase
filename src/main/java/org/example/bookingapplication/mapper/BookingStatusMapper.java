package org.example.bookingapplication.mapper;

import org.example.bookingapplication.config.MapperConfig;
import org.example.bookingapplication.dto.bookingstatus.response.BookingStatusDto;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookingStatusMapper {
    BookingStatusDto toDto(BookingStatus bookingStatus);
}
