package org.example.bookingapplication.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.accommodations.response.SizeTypeDto;
import org.example.bookingapplication.mapper.SizeTypeMapper;
import org.example.bookingapplication.model.accommodation.SizeType;
import org.example.bookingapplication.repository.sizetype.SizeTypeRepository;
import org.example.bookingapplication.service.SizeTypeService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SizeTypeServiceImpl implements SizeTypeService {
    private final SizeTypeRepository sizeTypeRepository;
    private final SizeTypeMapper sizeTypeMapper;

    @Override
    public List<SizeTypeDto> findAll() {
        List<SizeType> listOfTypes = sizeTypeRepository.findAll();
        return listOfTypes.stream()
                .map(sizeTypeMapper::toDto)
                .toList();
    }
}
