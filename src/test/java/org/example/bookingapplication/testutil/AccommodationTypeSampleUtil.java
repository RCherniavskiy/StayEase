package org.example.bookingapplication.testutil;

import org.example.bookingapplication.dto.accommodations.response.AccommodationTypeDto;
import org.example.bookingapplication.model.accommodation.AccommodationType;

public class AccommodationTypeSampleUtil {
    public static AccommodationType createSampleAccommodationType(
            Long id, AccommodationType.AccommodationTypeName accommodationTypeName) {
        AccommodationType accommodationType = new AccommodationType();
        accommodationType.setId(id);
        accommodationType.setName(accommodationTypeName);
        return accommodationType;
    }

    public static AccommodationTypeDto createSampleAccommodationTypeDto(
            Long id, AccommodationType.AccommodationTypeName accommodationTypeName) {
        AccommodationTypeDto accommodationTypeDto = new AccommodationTypeDto();
        accommodationTypeDto.setId(id);
        accommodationTypeDto.setName(accommodationTypeName.name());
        return accommodationTypeDto;
    }
}
