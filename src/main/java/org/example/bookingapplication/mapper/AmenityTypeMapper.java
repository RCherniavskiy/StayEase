package org.example.bookingapplication.mapper;

import org.example.bookingapplication.config.MapperConfig;
import org.example.bookingapplication.dto.accommodations.response.AmenityTypeDto;
import org.example.bookingapplication.model.accommodation.AmenityType;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AmenityTypeMapper {
    AmenityTypeDto toDto(AmenityType amenityType);
}
