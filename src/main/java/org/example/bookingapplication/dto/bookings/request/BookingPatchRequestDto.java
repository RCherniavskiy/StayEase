package org.example.bookingapplication.dto.bookings.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.bookingapplication.dto.checkdate.request.CheckDateRequestDto;

@Data
public class BookingPatchRequestDto {
    @NotNull
    private CheckDateRequestDto checkDates;
    @NotNull
    private Long accommodationId;
}
