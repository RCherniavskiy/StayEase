package org.example.bookingapplication.model.accommodation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "accommodation_types")
public class AccommodationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,
            unique = true,
            columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private AccommodationTypeName name;

    public enum AccommodationTypeName {
        HOUSE,
        APARTMENT,
        CONDO,
        VACATION_HOME,
        HOTEL,
        HOSTEL
    }
}
