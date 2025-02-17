package org.example.bookingapplication.repository.amenitytype;

import org.example.bookingapplication.model.accommodation.AmenityType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityTypeRepository extends JpaRepository<AmenityType, Long> {
}
