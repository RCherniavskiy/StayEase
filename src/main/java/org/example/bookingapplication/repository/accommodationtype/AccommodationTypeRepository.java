package org.example.bookingapplication.repository.accommodationtype;

import org.example.bookingapplication.model.accommodation.AccommodationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationTypeRepository extends JpaRepository<AccommodationType, Long> {
}
