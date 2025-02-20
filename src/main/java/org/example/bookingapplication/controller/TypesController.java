package org.example.bookingapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.accommodations.response.AccommodationTypeDto;
import org.example.bookingapplication.dto.accommodations.response.AmenityTypeDto;
import org.example.bookingapplication.dto.accommodations.response.SizeTypeDto;
import org.example.bookingapplication.dto.bookingstatus.response.BookingStatusDto;
import org.example.bookingapplication.dto.paymentstatuses.response.PaymentStatusDto;
import org.example.bookingapplication.service.AccommodationTypeService;
import org.example.bookingapplication.service.AmenityTypeService;
import org.example.bookingapplication.service.BookingStatusService;
import org.example.bookingapplication.service.PaymentStatusService;
import org.example.bookingapplication.service.SizeTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Types manage", description = "Endpoints for get type")
@RestController
@RequestMapping(value = "/types")
@RequiredArgsConstructor
public class TypesController {
    private final AccommodationTypeService accommodationTypeService;
    private final AmenityTypeService amenityTypeService;
    private final SizeTypeService sizeTypeService;
    private final PaymentStatusService paymentStatusService;
    private final BookingStatusService bookingStatusService;

    @GetMapping("/accommodation")
    @Operation(summary = "Get all accommodation types",
            description = "Get all accommodation types dto for front end")
    public List<AccommodationTypeDto> getAllAccommodationTypes() {
        return accommodationTypeService.findAll();
    }

    @GetMapping("/amenity")
    @Operation(summary = "Get all amenity types",
            description = "Get all amenity types dto for front end")
    public List<AmenityTypeDto> getAllAmenityTypes() {
        return amenityTypeService.findAll();
    }

    @GetMapping("/size")
    @Operation(summary = "Get all size types",
            description = "Get all size types dto for front end")
    public List<SizeTypeDto> getAllSizeTypes() {
        return sizeTypeService.findAll();
    }

    @GetMapping("/payment-status")
    @Operation(summary = "Get all payment status",
            description = "Get all payment status dto for front end")
    public List<PaymentStatusDto> getAllPaymentStatuses() {
        return paymentStatusService.findAll();
    }

    @GetMapping("/booking-status")
    @Operation(summary = "Get all booking status",
            description = "Get all booking status dto for front end")
    public List<BookingStatusDto> getAllBookingStatuses() {
        return bookingStatusService.findAll();
    }
}
