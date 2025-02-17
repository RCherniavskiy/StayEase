package org.example.bookingapplication.repository;

import org.example.bookingapplication.dto.accommodations.request.AccommodationSearchDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(AccommodationSearchDto requestDto);
}
