package org.example.bookingapplication.testutil;

import org.example.bookingapplication.dto.accommodations.response.AmenityTypeDto;
import org.example.bookingapplication.model.accommodation.AmenityType;

public class AmenityTypeSampleUtil {
    public static AmenityType createSampleAmenityType(
            Long id, AmenityType.AmenityTypeName amenityTypeName) {
        AmenityType amenityType = new AmenityType();
        amenityType.setId(id);
        amenityType.setName(amenityTypeName);
        return amenityType;
    }

    public static AmenityTypeDto createSampleAmenityTypeDto(
            Long id, AmenityType.AmenityTypeName amenityTypeName) {
        AmenityTypeDto amenityType = new AmenityTypeDto();
        amenityType.setId(id);
        amenityType.setName(amenityTypeName.name());
        return amenityType;
    }
}
