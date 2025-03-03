package org.example.bookingapplication.testutil;

import java.math.BigDecimal;
import java.util.Set;
import org.example.bookingapplication.dto.accommodations.request.AccommodationRequestDto;
import org.example.bookingapplication.dto.accommodations.response.AccommodationDto;
import org.example.bookingapplication.dto.accommodations.response.AccommodationTypeDto;
import org.example.bookingapplication.dto.accommodations.response.AmenityTypeDto;
import org.example.bookingapplication.dto.accommodations.response.SizeTypeDto;
import org.example.bookingapplication.dto.addresses.request.AddressRequestDto;
import org.example.bookingapplication.dto.addresses.response.AddressDto;
import org.example.bookingapplication.model.accommodation.AccommodationType;
import org.example.bookingapplication.model.accommodation.AmenityType;
import org.example.bookingapplication.model.accommodation.SizeType;

public class AccommodationControllerDtoUtil {
    public static AccommodationRequestDto getAccommodationRequestDto() {
        AddressRequestDto addressRequestDto = new AddressRequestDto();
        addressRequestDto.setCountry("Ukraine");
        addressRequestDto.setState("Kyiv Region");
        addressRequestDto.setCity("Kyiv");
        addressRequestDto.setStreet("Khreshchatyk St.");
        addressRequestDto.setHouseNumber("1");
        addressRequestDto.setZipCode("01001");

        AccommodationRequestDto accommodationRequestDto = new AccommodationRequestDto();
        accommodationRequestDto.setAddressDto(addressRequestDto);
        accommodationRequestDto.setTypeId(1L);
        accommodationRequestDto.setSizeId(1L);
        accommodationRequestDto.setDailyRate(BigDecimal.valueOf(120L));
        accommodationRequestDto.setAmenityTypeIds(Set.of(1L, 2L));

        return accommodationRequestDto;
    }

    public static AccommodationDto getFirstAccommodationResponseDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry("Ukraine");
        addressDto.setState("Kyiv Region");
        addressDto.setCity("Kyiv");
        addressDto.setStreet("Khreshchatyk St.");
        addressDto.setHouseNumber("1");
        addressDto.setZipCode("01001");

        AccommodationTypeDto accommodationTypeDto = new AccommodationTypeDto();
        accommodationTypeDto.setId(1L);
        accommodationTypeDto.setName(AccommodationType.AccommodationTypeName.HOUSE.name());

        SizeTypeDto sizeTypeDto = new SizeTypeDto();
        sizeTypeDto.setId(1L);
        sizeTypeDto.setName(SizeType.SizeTypeName.ONE_PEOPLE.name());

        AmenityTypeDto amenityTypeDto1 = new AmenityTypeDto();
        amenityTypeDto1.setId(1L);
        amenityTypeDto1.setName(AmenityType.AmenityTypeName.SWIMMING_POOL.name());

        AmenityTypeDto amenityTypeDto2 = new AmenityTypeDto();
        amenityTypeDto2.setId(2L);
        amenityTypeDto2.setName(AmenityType.AmenityTypeName.GYM.name());

        AccommodationDto accommodationDto = new AccommodationDto();
        accommodationDto.setId(1L);
        accommodationDto.setAddress(addressDto);
        accommodationDto.setType(accommodationTypeDto);
        accommodationDto.setSize(sizeTypeDto);
        accommodationDto.setDailyRate(BigDecimal.valueOf(120L));
        accommodationDto.setAmenities(Set.of(amenityTypeDto1, amenityTypeDto2));

        return accommodationDto;
    }
}
