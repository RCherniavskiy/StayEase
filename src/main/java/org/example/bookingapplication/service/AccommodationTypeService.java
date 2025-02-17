package org.example.bookingapplication.service;

import java.util.List;
import org.example.bookingapplication.dto.accommodations.response.AccommodationTypeDto;

public interface AccommodationTypeService {
    List<AccommodationTypeDto> findAll();
}
