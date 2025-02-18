package org.example.bookingapplication.mapper;

import org.example.bookingapplication.config.MapperConfig;
import org.example.bookingapplication.dto.checkdate.request.CheckDateRequestDto;
import org.example.bookingapplication.model.booking.CheckDate;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CheckDateMapper {
    CheckDate toModel(CheckDateRequestDto requestDto);

    CheckDateRequestDto toDto(CheckDate checkDate);
}
