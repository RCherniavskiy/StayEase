package org.example.bookingapplication.mapper;

import org.example.bookingapplication.config.MapperConfig;
import org.example.bookingapplication.dto.addresses.request.AddressRequestDto;
import org.example.bookingapplication.dto.addresses.response.AddressDto;
import org.example.bookingapplication.model.accommodation.Address;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AddressMapper {
    AddressDto toDto(Address address);

    Address toModel(AddressRequestDto requestDto);
}
