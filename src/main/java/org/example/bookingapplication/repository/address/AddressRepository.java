package org.example.bookingapplication.repository.address;

import org.example.bookingapplication.model.accommodation.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
