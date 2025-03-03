package org.example.bookingapplication.testutil;

import org.example.bookingapplication.dto.accommodations.response.SizeTypeDto;
import org.example.bookingapplication.model.accommodation.SizeType;

public class SizeTypeSampleUtil {
    public static SizeType createSampleSizeType(Long id, SizeType.SizeTypeName sizeTypeName) {
        SizeType sizeType = new SizeType();
        sizeType.setId(id);
        sizeType.setName(sizeTypeName);
        return sizeType;
    }

    public static SizeTypeDto createSampleSizeTypeDto(Long id, SizeType.SizeTypeName sizeTypeName) {
        SizeTypeDto sizeTypeDto = new SizeTypeDto();
        sizeTypeDto.setId(id);
        sizeTypeDto.setName(sizeTypeName.toString());
        return sizeTypeDto;
    }
}
