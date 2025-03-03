package org.example.bookingapplication.testutil;

import java.math.BigDecimal;
import java.util.Set;
import org.example.bookingapplication.dto.accommodations.request.AccommodationRequestDto;
import org.example.bookingapplication.dto.accommodations.response.AccommodationDto;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.model.accommodation.AccommodationType;
import org.example.bookingapplication.model.accommodation.AmenityType;
import org.example.bookingapplication.model.accommodation.SizeType;

public class AccommodationSampleUtil {
    public static AccommodationRequestDto getRequestDto(Long param) {
        AccommodationRequestDto requestDto = new AccommodationRequestDto();
        requestDto.setTypeId(1L);
        requestDto.setAddressDto(AddressSampleUtil.createSampleAddressRequestDto(param));
        requestDto.setSizeId(1L);
        requestDto.setAmenityTypeIds(Set.of(1L));
        requestDto.setDailyRate(BigDecimal.valueOf(param));
        return requestDto;
    }

    public static Accommodation getAccommodationModel(Long param) {
        Accommodation accommodation = new Accommodation();
        accommodation.setType(AccommodationTypeSampleUtil.createSampleAccommodationType(
                1L, AccommodationType.AccommodationTypeName.HOSTEL));
        accommodation.setAddress(AddressSampleUtil.createSampleAddress(param));
        accommodation.setSize(SizeTypeSampleUtil.createSampleSizeType(
                1L, SizeType.SizeTypeName.ONE_PEOPLE));
        accommodation.setAmenities(Set.of(AmenityTypeSampleUtil.createSampleAmenityType(
                1L, AmenityType.AmenityTypeName.FREE_WIFI)));
        accommodation.setDailyRate(BigDecimal.valueOf(param));
        return accommodation;
    }

    public static AccommodationDto getAccommodationDto(Long param) {
        AccommodationDto accommodationDto = new AccommodationDto();
        accommodationDto.setType(AccommodationTypeSampleUtil.createSampleAccommodationTypeDto(
                1L, AccommodationType.AccommodationTypeName.HOSTEL));
        accommodationDto.setAddress(AddressSampleUtil.createSampleAddressDto(param));
        accommodationDto.setSize(SizeTypeSampleUtil.createSampleSizeTypeDto(
                1L, SizeType.SizeTypeName.ONE_PEOPLE));
        accommodationDto.setAmenities(Set.of(AmenityTypeSampleUtil.createSampleAmenityTypeDto(
                1L, AmenityType.AmenityTypeName.FREE_WIFI)));
        accommodationDto.setDailyRate(BigDecimal.valueOf(param));
        return accommodationDto;
    }
}
