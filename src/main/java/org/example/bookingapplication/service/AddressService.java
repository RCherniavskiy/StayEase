package org.example.bookingapplication.service;

import org.example.bookingapplication.dto.addresses.request.AddressRequestDto;
import org.example.bookingapplication.model.accommodation.Address;

public interface AddressService {
    Address save(AddressRequestDto requestDto);
}
