package org.example.bookingapplication.service;

import java.util.List;
import org.example.bookingapplication.dto.accommodations.response.SizeTypeDto;

public interface SizeTypeService {
    List<SizeTypeDto> findAll();
}
