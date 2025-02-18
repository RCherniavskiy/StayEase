package org.example.bookingapplication.dto.bookings.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.example.bookingapplication.dto.checkdate.request.CheckDateRequestDto;

@Data
public class BookingRequestDto {
    @NotNull
    private CheckDateRequestDto checkDates;
    @Positive
    private Long accommodationId;
}
