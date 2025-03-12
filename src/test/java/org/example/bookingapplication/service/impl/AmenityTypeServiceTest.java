package org.example.bookingapplication.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.example.bookingapplication.dto.accommodations.response.AmenityTypeDto;
import org.example.bookingapplication.mapper.AmenityTypeMapper;
import org.example.bookingapplication.model.accommodation.AmenityType;
import org.example.bookingapplication.repository.amenitytype.AmenityTypeRepository;
import org.example.bookingapplication.testutil.AmenityTypeSampleUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AmenityTypeServiceTest {
    @Mock
    private AmenityTypeRepository amenityTypeRepository;
    @Mock
    private AmenityTypeMapper amenityTypeMapper;
    @InjectMocks
    private AmenityTypeServiceImpl amenityTypeService;

    @Test
    @DisplayName("Find all amenity types with exist data")
    void findAll_findExistData_ReturnTwoObjects() {
        // Given: A list of amenity types and their corresponding DTOs
        AmenityType amenityType1 = AmenityTypeSampleUtil
                .createSampleAmenityType(1L, AmenityType.AmenityTypeName.FREE_WIFI);
        AmenityType amenityType2 = AmenityTypeSampleUtil
                .createSampleAmenityType(2L, AmenityType.AmenityTypeName.PET_FRIENDLY);
        AmenityTypeDto amenityTypeDto1 = AmenityTypeSampleUtil
                .createSampleAmenityTypeDto(1L, AmenityType.AmenityTypeName.FREE_WIFI);
        AmenityTypeDto amenityTypeDto2 = AmenityTypeSampleUtil
                .createSampleAmenityTypeDto(2L, AmenityType.AmenityTypeName.PET_FRIENDLY);

        when(amenityTypeRepository.findAll()).thenReturn(List.of(amenityType1, amenityType2));
        when(amenityTypeMapper.toDto(amenityType1)).thenReturn(amenityTypeDto1);
        when(amenityTypeMapper.toDto(amenityType2)).thenReturn(amenityTypeDto2);

        // When: Calling the findAll method
        List<AmenityTypeDto> result = amenityTypeService.findAll();

        // Then: Verify the expected output
        assertEquals(2, result.size());
        assertEquals(amenityTypeDto1, result.get(0));
        assertEquals(amenityTypeDto2, result.get(1));

        verify(amenityTypeRepository, times(1)).findAll();
        verify(amenityTypeMapper, times(1)).toDto(amenityType1);
        verify(amenityTypeMapper, times(1)).toDto(amenityType2);
        verifyNoMoreInteractions(amenityTypeRepository);
        verifyNoMoreInteractions(amenityTypeMapper);
    }
}
