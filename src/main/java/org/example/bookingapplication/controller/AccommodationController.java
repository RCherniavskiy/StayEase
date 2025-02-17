package org.example.bookingapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.accommodations.request.AccommodationRequestDto;
import org.example.bookingapplication.dto.accommodations.request.AccommodationSearchDto;
import org.example.bookingapplication.dto.accommodations.response.AccommodationDto;
import org.example.bookingapplication.service.AccommodationService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodations management", description = "Endpoints to managing accommodations")
@RestController
@RequestMapping(value = "/accommodations")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;

    @GetMapping
    @Operation(summary = "Get all accommodations",
            description = "Get a page of all available accommodations")
    public List<AccommodationDto> getAll(Pageable pageable) {
        return accommodationService.findAll(pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Get all accommodations with parameters",
            description = "Get a page of all available accommodations with parameters")
    public List<AccommodationDto> search(Pageable pageable,
                                         @Valid AccommodationSearchDto requestDto) {
        return accommodationService.search(pageable, requestDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get accommodation by id",
            description = "Get existing accommodation by id")
    public AccommodationDto getById(@PathVariable Long id) {
        return accommodationService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update accommodation by id",
            description = "Update existing accommodation field by id")
    public AccommodationDto update(@PathVariable Long id,
                                   @RequestBody @Valid AccommodationRequestDto requestDto) {
        return accommodationService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete accommodation by id",
            description = "Delete existing accommodation by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create accommodation",
            description = "Create a new accommodation")
    @ResponseStatus(HttpStatus.CREATED)
    public AccommodationDto save(@RequestBody @Valid AccommodationRequestDto requestDto) {
        return accommodationService.save(requestDto);
    }
}
