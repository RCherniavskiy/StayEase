package org.example.bookingapplication.dto.accommodations.response;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import org.example.bookingapplication.dto.addresses.response.AddressDto;

@Data
public class AccommodationDto {
    private Long id;
    private AccommodationTypeDto type;
    private AddressDto address;
    private SizeTypeDto size;
    private Set<AmenityTypeDto> amenities;
    private BigDecimal dailyRate;
}
