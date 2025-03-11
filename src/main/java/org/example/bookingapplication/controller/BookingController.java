package org.example.bookingapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.bookings.request.BookingRequestDto;
import org.example.bookingapplication.dto.bookings.responce.BookingDto;
import org.example.bookingapplication.service.BookingService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking management", description = "Endpoints to managing bookings")
@RestController
@RequestMapping(value = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Booking",
            description = "create a bookings")
    public BookingDto save(@RequestBody @Valid BookingRequestDto requestDto,
                           Authentication authentication) {
        return bookingService.save(requestDto, authentication.getName());
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Get all bookings",
            description = "Get a page of all available user bookings")
    public List<BookingDto> getUserBookings(Authentication authentication, Pageable pageable) {
        return bookingService.findByUserEmail(authentication.getName(), pageable);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all bookings",
            description = "Get a page of all available bookings")
    public List<BookingDto> getAllBookings(Pageable pageable) {
        return bookingService.findAllBookings(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Get booking by id",
            description = "Get a booking by id if this you booking")
    public BookingDto getBookingsById(@PathVariable Long id, Authentication authentication) {
        return bookingService.getById(id, authentication.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Update bookings",
            description = "Update booking status, checkIn and checkOut date")
    public BookingDto update(@PathVariable Long id,
                             @RequestBody @Valid BookingRequestDto requestDto,
                             Authentication authentication) {
        return bookingService.updateInfo(id, requestDto, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Cancel bookings",
            description = "Cancel booking if is have status pending")
    public BookingDto cancelBookingById(@PathVariable Long id,
                                        Authentication authentication) {
        return bookingService.cancel(id, authentication.getName());
    }
}
