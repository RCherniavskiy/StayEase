package org.example.bookingapplication.model.accommodation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "amenity_types")
public class AmenityType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name",
            nullable = false,
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
