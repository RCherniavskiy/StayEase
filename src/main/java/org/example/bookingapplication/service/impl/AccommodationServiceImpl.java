package org.example.bookingapplication.service.impl;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.accommodations.request.AccommodationRequestDto;
import org.example.bookingapplication.dto.accommodations.request.AccommodationSearchDto;
import org.example.bookingapplication.dto.accommodations.response.AccommodationDto;
import org.example.bookingapplication.dto.addresses.request.AddressRequestDto;
import org.example.bookingapplication.exception.repository.EntityNotFoundException;
import org.example.bookingapplication.mapper.AccommodationMapper;
import org.example.bookingapplication.mapper.AddressMapper;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.model.accommodation.AccommodationType;
import org.example.bookingapplication.model.accommodation.Address;
import org.example.bookingapplication.model.accommodation.AmenityType;
import org.example.bookingapplication.model.accommodation.SizeType;
import org.example.bookingapplication.repository.accommodation.AccommodationRepository;
import org.example.bookingapplication.repository.accommodation.AccommodationSpecificationBuilder;
import org.example.bookingapplication.repository.accommodationtype.AccommodationTypeRepository;
import org.example.bookingapplication.repository.address.AddressRepository;
import org.example.bookingapplication.repository.amenitytype.AmenityTypeRepository;
import org.example.bookingapplication.repository.sizetype.SizeTypeRepository;
import org.example.bookingapplication.service.AccommodationService;
import org.example.bookingapplication.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final AddressService addressService;
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;
    private final AccommodationSpecificationBuilder accommodationSpecificationBuilder;
    private final AccommodationTypeRepository accommodationTypeRepository;
    private final SizeTypeRepository sizeTypeRepository;
    private final AmenityTypeRepository amenityTypeRepository;

    @Override
    public AccommodationDto save(AccommodationRequestDto requestDto) {
        Accommodation accommodation = getAccommodationWithAddress(requestDto);
        setSizeToAccommodation(accommodation, requestDto.getSizeId());
        setTypeToAccommodation(accommodation, requestDto.getTypeId());
        setAmenitiesToAccommodation(accommodation, requestDto.getAmenityTypeIds());
        accommodationRepository.save(accommodation);
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public AccommodationDto getById(Long id) {
        return accommodationMapper.toDto(getAccommodationById(id));
    }

    @Override
    public List<AccommodationDto> search(Pageable pageable, AccommodationSearchDto requestDto) {
        Specification<Accommodation> accommodationSpecification =
                accommodationSpecificationBuilder.build(requestDto);
        Page<Accommodation> accommodations = accommodationRepository.findAll(
                accommodationSpecification, pageable);
        return accommodations.stream().map(accommodationMapper::toDto).toList();
    }

    @Override
    public List<AccommodationDto> findAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable).stream().map(
                accommodationMapper::toDto).toList();
    }

    @Override
    public AccommodationDto update(Long id, AccommodationRequestDto requestDto) {
        Accommodation accommodation = getAccommodationById(id);
        updateAccommodationWithoutAddress(accommodation, requestDto);
        updateAccommodationAddress(accommodation, requestDto);
        accommodationRepository.save(accommodation);
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public void deleteById(Long id) {
        getAccommodationById(id);
        accommodationRepository.deleteById(id);
    }

    private void updateAccommodationWithoutAddress(Accommodation accommodation,
                                                   AccommodationRequestDto requestDto) {
        Accommodation updateAccommodation =
                accommodationMapper.toModelWithoutAddressAndTypes(requestDto);
        setSizeToAccommodation(updateAccommodation, requestDto.getSizeId());
        setTypeToAccommodation(updateAccommodation, requestDto.getTypeId());
        setAmenitiesToAccommodation(updateAccommodation, requestDto.getAmenityTypeIds());
        BeanUtils.copyProperties(updateAccommodation, accommodation, "id", "address", "isDeleted");
    }

    private void updateAccommodationAddress(Accommodation accommodation,
                                            AccommodationRequestDto requestDto) {
        Address address = accommodation.getAddress();
        Address updateAddress = addressMapper.toModel(requestDto.getAddressDto());
        BeanUtils.copyProperties(updateAddress, address, "id", "isDeleted");
        accommodation.setAddress(addressRepository.save(address)); // сохраняем только если нужно
    }

    private Accommodation getAccommodationById(Long id) {
        return accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cant find accommodation with id: " + id));
    }

    private Accommodation getAccommodationWithAddress(
            AccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModelWithoutAddressAndTypes(requestDto);
        AddressRequestDto addressRequestDto = requestDto.getAddressDto();
        Address address = addressService.save(addressRequestDto);
        accommodation.setAddress(address);
        return accommodation;
    }

    private void setSizeToAccommodation(Accommodation accommodation, Long sizeId) {
        SizeType sizeType = sizeTypeRepository.findById(sizeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cant find size type with id: " + sizeId));
        accommodation.setSize(sizeType);
    }

    private void setTypeToAccommodation(Accommodation accommodation, Long typeId) {
        AccommodationType type = accommodationTypeRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cant find accommodation type with id: " + typeId));
        accommodation.setType(type);
    }

    private void setAmenitiesToAccommodation(Accommodation accommodation, Set<Long> amenityIds) {
        List<AmenityType> amenityTypes = amenityTypeRepository.findAllById(amenityIds);
        if (amenityTypes.isEmpty()) {
            throw new EntityNotFoundException("Cant find amenity types with id's: " + amenityIds);
        }
        accommodation.setAmenities(new HashSet<>(amenityTypes));
    }
}
