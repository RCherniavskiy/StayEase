package org.example.bookingapplication.dto.addresses.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequestDto {
    @NotBlank
    private String country;
    @NotBlank
    private String state;
    @NotBlank
    private String city;
    private String district;
    @NotBlank
    private String street;
    @NotBlank
    private String houseNumber;
    private String apartmentNumber;
    @NotBlank
    private String floor;
    @NotBlank
    private String zipCode;
}
