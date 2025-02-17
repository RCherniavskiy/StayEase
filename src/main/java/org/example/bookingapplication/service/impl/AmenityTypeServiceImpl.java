package org.example.bookingapplication.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.accommodations.response.AmenityTypeDto;
import org.example.bookingapplication.mapper.AmenityTypeMapper;
import org.example.bookingapplication.model.accommodation.AmenityType;
import org.example.bookingapplication.repository.amenitytype.AmenityTypeRepository;
import org.example.bookingapplication.service.AmenityTypeService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmenityTypeServiceImpl implements AmenityTypeService {
    private final AmenityTypeRepository amenityTypeRepository;
    private final AmenityTypeMapper amenityTypeMapper;

    @Override
    public List<AmenityTypeDto> findAll() {
        List<AmenityType> listOfTypes = amenityTypeRepository.findAll();
        return listOfTypes.stream()
                .map(amenityTypeMapper::toDto)
                .toList();
    }
}
