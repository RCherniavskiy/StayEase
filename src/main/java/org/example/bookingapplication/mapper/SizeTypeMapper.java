package org.example.bookingapplication.mapper;

import org.example.bookingapplication.config.MapperConfig;
import org.example.bookingapplication.dto.accommodations.response.SizeTypeDto;
import org.example.bookingapplication.model.accommodation.SizeType;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface SizeTypeMapper {
    SizeTypeDto toDto(SizeType sizeType);
}
