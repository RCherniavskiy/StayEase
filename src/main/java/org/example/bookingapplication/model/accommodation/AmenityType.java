package org.example.bookingapplication.model.accommodation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "amenity_types")
public class AmenityType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,
            unique = true,
            columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private AmenityTypeName name;

    public enum AmenityTypeName {
        SWIMMING_POOL,
        GYM,
        FREE_WIFI,
        PARKING,
        BREAKFAST_INCLUDED,
        PET_FRIENDLY
    }
}
