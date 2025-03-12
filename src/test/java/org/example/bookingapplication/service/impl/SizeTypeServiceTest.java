package org.example.bookingapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.example.bookingapplication.dto.accommodations.response.SizeTypeDto;
import org.example.bookingapplication.mapper.SizeTypeMapper;
import org.example.bookingapplication.model.accommodation.SizeType;
import org.example.bookingapplication.repository.sizetype.SizeTypeRepository;
import org.example.bookingapplication.testutil.SizeTypeSampleUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SizeTypeServiceTest {
    @Mock
    private SizeTypeRepository sizeTypeRepository;
    @Mock
    private SizeTypeMapper sizeTypeMapper;
    @InjectMocks
    private SizeTypeServiceImpl sizeTypeService;

    @Test
    @DisplayName("Find all size type with exist data")
    void findAll_findExistData_ReturnTwoObjects() {
        // Given: Two size types exist in the database
        SizeType sizeType1 = SizeTypeSampleUtil
                .createSampleSizeType(1L, SizeType.SizeTypeName.TWO_PEOPLE);
        SizeType sizeType2 = SizeTypeSampleUtil
                .createSampleSizeType(2L, SizeType.SizeTypeName.ONE_PEOPLE);
        SizeTypeDto sizeTypeDto1 = SizeTypeSampleUtil
                .createSampleSizeTypeDto(1L, SizeType.SizeTypeName.TWO_PEOPLE);
        SizeTypeDto sizeTypeDto2 = SizeTypeSampleUtil
                .createSampleSizeTypeDto(2L, SizeType.SizeTypeName.ONE_PEOPLE);

        when(sizeTypeRepository.findAll()).thenReturn(List.of(sizeType1, sizeType2));
        when(sizeTypeMapper.toDto(sizeType1)).thenReturn(sizeTypeDto1);
        when(sizeTypeMapper.toDto(sizeType2)).thenReturn(sizeTypeDto2);

        // When: Retrieving all size types
        List<SizeTypeDto> result = sizeTypeService.findAll();

        // Then: The result should contain two elements with correct data
        assertEquals(2, result.size());
        assertEquals(sizeTypeDto1, result.get(0));
        assertEquals(sizeTypeDto2, result.get(1));

        // Verify that the repository and mapper were called the expected number of times
        verify(sizeTypeRepository, times(1)).findAll();
        verify(sizeTypeMapper, times(1)).toDto(sizeType1);
        verify(sizeTypeMapper, times(1)).toDto(sizeType2);
        verifyNoMoreInteractions(sizeTypeRepository);
        verifyNoMoreInteractions(sizeTypeMapper);
    }
}
