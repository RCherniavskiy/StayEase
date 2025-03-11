package org.example.bookingapplication.dto.accommodations.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import org.example.bookingapplication.dto.addresses.request.AddressRequestDto;

@Data
public class AccommodationRequestDto {
    @NotNull
    @Positive
    private Long typeId;
    @NotNull
    private AddressRequestDto addressDto;
    @NotNull
    @Positive
    private Long sizeId;
    @NotEmpty
    private Set<Long> amenityTypeIds;
    @NotNull
    @Positive
    private BigDecimal dailyRate;
}
