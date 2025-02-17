package org.example.bookingapplication.service;

import java.util.List;
import org.example.bookingapplication.dto.accommodations.request.AccommodationRequestDto;
import org.example.bookingapplication.dto.accommodations.request.AccommodationSearchDto;
import org.example.bookingapplication.dto.accommodations.response.AccommodationDto;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    AccommodationDto save(AccommodationRequestDto requestDto);

    AccommodationDto getById(Long id);

    List<AccommodationDto> findAll(Pageable pageable);

    List<AccommodationDto> search(Pageable pageable, AccommodationSearchDto requestDto);

    AccommodationDto update(Long id, AccommodationRequestDto requestDto);

    void deleteById(Long id);
}
