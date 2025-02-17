package org.example.bookingapplication.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.accommodations.response.AccommodationTypeDto;
import org.example.bookingapplication.mapper.AccommodationTypeMapper;
import org.example.bookingapplication.model.accommodation.AccommodationType;
import org.example.bookingapplication.repository.accommodationtype.AccommodationTypeRepository;
import org.example.bookingapplication.service.AccommodationTypeService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationTypeServiceImpl implements AccommodationTypeService {
    private final AccommodationTypeRepository accommodationTypeRepository;
    private final AccommodationTypeMapper accommodationTypeMapper;

    @Override
    public List<AccommodationTypeDto> findAll() {
        List<AccommodationType> listOfTypes = accommodationTypeRepository.findAll();
        return listOfTypes.stream()
                .map(accommodationTypeMapper::toDto)
                .toList();
    }
}
