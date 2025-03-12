package org.example.bookingapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.example.bookingapplication.dto.accommodations.response.AccommodationTypeDto;
import org.example.bookingapplication.mapper.AccommodationTypeMapper;
import org.example.bookingapplication.model.accommodation.AccommodationType;
import org.example.bookingapplication.repository.accommodationtype.AccommodationTypeRepository;
import org.example.bookingapplication.testutil.AccommodationTypeSampleUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccommodationTypeServiceTest {
    @Mock
    private AccommodationTypeRepository accommodationTypeRepository;
    @Mock
    private AccommodationTypeMapper accommodationTypeMapper;
    @InjectMocks
    private AccommodationTypeServiceImpl accommodationTypeService;

    @Test
    @DisplayName("Find all accommodation types with exist data")
    void findAll_findExistData_ReturnTwoObjects() {
        // Given: There are two accommodation types in the database
        AccommodationType accommodationType1 = AccommodationTypeSampleUtil
                .createSampleAccommodationType(1L, AccommodationType.AccommodationTypeName.HOSTEL);
        AccommodationType accommodationType2 = AccommodationTypeSampleUtil
                .createSampleAccommodationType(2L, AccommodationType.AccommodationTypeName.HOTEL);
        AccommodationTypeDto accommodationTypeDto1 = AccommodationTypeSampleUtil
                .createSampleAccommodationTypeDto(
                        1L, AccommodationType.AccommodationTypeName.HOSTEL);
        AccommodationTypeDto accommodationTypeDto2 = AccommodationTypeSampleUtil
                .createSampleAccommodationTypeDto(
                        2L, AccommodationType.AccommodationTypeName.HOTEL);

        when(accommodationTypeRepository.findAll()).thenReturn(
                List.of(accommodationType1, accommodationType2));
        when(accommodationTypeMapper.toDto(accommodationType1)).thenReturn(accommodationTypeDto1);
        when(accommodationTypeMapper.toDto(accommodationType2)).thenReturn(accommodationTypeDto2);

        // When: Calling the `findAll()` method
        List<AccommodationTypeDto> result = accommodationTypeService.findAll();

        // Then: Verify that the result matches expectations
        assertEquals(2, result.size());
        assertEquals(accommodationTypeDto1, result.get(0));
        assertEquals(accommodationTypeDto2, result.get(1));

        verify(accommodationTypeRepository, times(1)).findAll();
        verify(accommodationTypeMapper, times(1)).toDto(accommodationType1);
        verify(accommodationTypeMapper, times(1)).toDto(accommodationType2);
        verifyNoMoreInteractions(accommodationTypeRepository);
        verifyNoMoreInteractions(accommodationTypeMapper);
    }
}
