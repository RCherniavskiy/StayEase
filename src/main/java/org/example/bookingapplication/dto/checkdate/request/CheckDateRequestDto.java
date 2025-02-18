package org.example.bookingapplication.dto.checkdate.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class CheckDateRequestDto {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
