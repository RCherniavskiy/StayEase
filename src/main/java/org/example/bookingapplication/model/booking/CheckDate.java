package org.example.bookingapplication.model.booking;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class CheckDate {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
