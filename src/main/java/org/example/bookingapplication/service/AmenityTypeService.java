package org.example.bookingapplication.service;

import java.util.List;
import org.example.bookingapplication.dto.accommodations.response.AmenityTypeDto;

public interface AmenityTypeService {
    List<AmenityTypeDto> findAll();
}
