package org.example.bookingapplication.dto.accommodations.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import org.example.bookingapplication.validation.CheckDateValues;

@Data
public class AccommodationSearchDto {
    private Long[] typeIds;
    private Long[] sizeTypeIds;
    private Long[] amenityTypeIds;
    private BigDecimal dailyRateMin;
    private BigDecimal dailyRateMax;
    @CheckDateValues
    private LocalDate[] checkDates;
}
