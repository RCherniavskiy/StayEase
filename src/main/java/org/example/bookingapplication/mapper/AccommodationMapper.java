package org.example.bookingapplication.mapper;

import org.example.bookingapplication.config.MapperConfig;
import org.example.bookingapplication.dto.accommodations.request.AccommodationRequestDto;
import org.example.bookingapplication.dto.accommodations.response.AccommodationDto;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {
        AddressMapper.class,
        AccommodationTypeMapper.class,
        AmenityTypeMapper.class,
        SizeTypeMapper.class
})
public interface AccommodationMapper {
    AccommodationDto toDto(Accommodation accommodation);

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "size", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "address", ignore = true)
    Accommodation toModelWithoutAddressAndTypes(AccommodationRequestDto requestDto);
}
