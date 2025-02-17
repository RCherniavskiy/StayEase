package org.example.bookingapplication.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.addresses.request.AddressRequestDto;
import org.example.bookingapplication.mapper.AddressMapper;
import org.example.bookingapplication.model.accommodation.Address;
import org.example.bookingapplication.repository.address.AddressRepository;
import org.example.bookingapplication.service.AddressService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public Address save(AddressRequestDto requestDto) {
        Address address = addressMapper.toModel(requestDto);
        return addressRepository.save(address);
    }
}
