package org.example.bookingapplication.mapper;

import org.example.bookingapplication.config.MapperConfig;
import org.example.bookingapplication.dto.accommodations.response.AccommodationTypeDto;
import org.example.bookingapplication.model.accommodation.AccommodationType;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AccommodationTypeMapper {
    AccommodationTypeDto toDto(AccommodationType accommodationType);
}
