package org.example.bookingapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.example.bookingapplication.dto.addresses.request.AddressRequestDto;
import org.example.bookingapplication.mapper.AddressMapper;
import org.example.bookingapplication.model.accommodation.Address;
import org.example.bookingapplication.repository.address.AddressRepository;
import org.example.bookingapplication.testutil.AddressSampleUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AddressMapper addressMapper;
    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    @DisplayName("Save valid address")
    void save_saveValidData_ReturnSavedAddress() {
        AddressRequestDto requestDto = AddressSampleUtil.createSampleAddressRequestDto(1L);
        Address address = AddressSampleUtil.createSampleAddress(1L);

        when(addressMapper.toModel(requestDto)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);

        Address result = addressService.save(requestDto);
        assertNotNull(result);
        assertEquals(address, result);

        verify(addressMapper, times(1)).toModel(requestDto);
        verify(addressRepository, times(1)).save(address);
        verifyNoMoreInteractions(addressMapper);
        verifyNoMoreInteractions(addressRepository);
    }
}
