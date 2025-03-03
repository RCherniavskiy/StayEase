package org.example.bookingapplication.testutil;

import org.example.bookingapplication.dto.addresses.request.AddressRequestDto;
import org.example.bookingapplication.dto.addresses.response.AddressDto;
import org.example.bookingapplication.model.accommodation.Address;

public class AddressSampleUtil {

    public static AddressRequestDto createSampleAddressRequestDto(Long param) {
        AddressRequestDto requestDto = new AddressRequestDto();
        requestDto.setCountry("Ukraine" + param);
        requestDto.setState("Kyiv" + param);
        requestDto.setCity("Kyiv" + param);
        requestDto.setDistrict("Shevchenkivskyi" + param);
        requestDto.setStreet("Khreshchatyk" + param);
        requestDto.setHouseNumber("1" + param);
        requestDto.setApartmentNumber("23" + param);
        requestDto.setFloor("5" + param);
        requestDto.setZipCode("01001" + param);
        return requestDto;
    }

    public static Address createSampleAddress(Long param) {
        Address address = new Address();
        address.setCountry("Ukraine" + param);
        address.setState("Kyiv" + param);
        address.setCity("Kyiv" + param);
        address.setDistrict("Shevchenkivskyi" + param);
        address.setStreet("Khreshchatyk" + param);
        address.setHouseNumber("1" + param);
        address.setApartmentNumber("23" + param);
        address.setFloor("5" + param);
        address.setZipCode("01001" + param);
        return address;
    }

    public static AddressDto createSampleAddressDto(Long param) {
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry("Ukraine" + param);
        addressDto.setState("Kyiv" + param);
        addressDto.setCity("Kyiv" + param);
        addressDto.setDistrict("Shevchenkivskyi" + param);
        addressDto.setStreet("Khreshchatyk" + param);
        addressDto.setHouseNumber("1" + param);
        addressDto.setApartmentNumber("23" + param);
        addressDto.setFloor("5" + param);
        addressDto.setZipCode("01001" + param);
        return addressDto;
    }
}
