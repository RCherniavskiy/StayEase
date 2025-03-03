package org.example.bookingapplication.service.impl;

import static org.example.bookingapplication.testutil.AccommodationSampleUtil.getAccommodationModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.example.bookingapplication.dto.accommodations.request.AccommodationRequestDto;
import org.example.bookingapplication.dto.accommodations.request.AccommodationSearchDto;
import org.example.bookingapplication.dto.accommodations.response.AccommodationDto;
import org.example.bookingapplication.exception.repository.EntityNotFoundException;
import org.example.bookingapplication.mapper.AccommodationMapper;
import org.example.bookingapplication.mapper.AddressMapper;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.repository.accommodation.AccommodationRepository;
import org.example.bookingapplication.repository.accommodation.AccommodationSpecificationBuilder;
import org.example.bookingapplication.repository.accommodationtype.AccommodationTypeRepository;
import org.example.bookingapplication.repository.address.AddressRepository;
import org.example.bookingapplication.repository.amenitytype.AmenityTypeRepository;
import org.example.bookingapplication.repository.sizetype.SizeTypeRepository;
import org.example.bookingapplication.service.AddressService;
import org.example.bookingapplication.testutil.AccommodationSampleUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private AccommodationMapper accommodationMapper;
    @Mock
    private AddressService addressService;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AccommodationSpecificationBuilder accommodationSpecificationBuilder;
    @Mock
    private AccommodationTypeRepository accommodationTypeRepository;
    @Mock
    private SizeTypeRepository sizeTypeRepository;
    @Mock
    private AmenityTypeRepository amenityTypeRepository;
    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    @Test
    @DisplayName("Save accommodation with valid data")
    void save_saveAccommodationWithValidData_ReturnAccommodation() {
        AccommodationRequestDto requestDto = AccommodationSampleUtil.getRequestDto(1L);
        Accommodation accommodationModel = AccommodationSampleUtil.getAccommodationModel(1L);
        AccommodationDto responseDto = AccommodationSampleUtil.getAccommodationDto(1L);

        when(accommodationMapper.toModelWithoutAddressAndTypes(requestDto))
                .thenReturn(accommodationModel);
        when(addressService.save(requestDto.getAddressDto()))
                .thenReturn(accommodationModel.getAddress());
        when(sizeTypeRepository.findById(requestDto.getSizeId()))
                .thenReturn(Optional.of(accommodationModel.getSize()));
        when(accommodationTypeRepository.findById(requestDto.getTypeId()))
                .thenReturn(Optional.of(accommodationModel.getType()));
        when(amenityTypeRepository.findAllById(requestDto.getAmenityTypeIds()))
                .thenReturn(accommodationModel.getAmenities().stream().toList());
        when(accommodationRepository.save(accommodationModel)).thenReturn(accommodationModel);
        when(accommodationMapper.toDto(accommodationModel)).thenReturn(responseDto);

        AccommodationDto result = accommodationService.save(requestDto);

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(accommodationMapper, times(1)).toModelWithoutAddressAndTypes(requestDto);
        verify(addressService, times(1)).save(requestDto.getAddressDto());
        verify(sizeTypeRepository, times(1)).findById(requestDto.getSizeId());
        verify(accommodationTypeRepository, times(1)).findById(requestDto.getTypeId());
        verify(amenityTypeRepository, times(1)).findAllById(requestDto.getAmenityTypeIds());
        verify(accommodationRepository, times(1)).save(accommodationModel);
        verify(accommodationMapper, times(1)).toDto(accommodationModel);

        verifyNoMoreInteractions(
                accommodationMapper,
                addressService,
                sizeTypeRepository,
                accommodationTypeRepository,
                amenityTypeRepository,
                accommodationRepository
        );
    }

    @Test
    @DisplayName("Save accommodation with not valid size type")
    void save_saveAccommodationWithNotValidSize_ThrowException() {
        AccommodationRequestDto requestDto = AccommodationSampleUtil.getRequestDto(1L);
        requestDto.setSizeId(null);
        Accommodation accommodationModel = AccommodationSampleUtil.getAccommodationModel(1L);

        when(accommodationMapper.toModelWithoutAddressAndTypes(requestDto))
                .thenReturn(accommodationModel);
        when(addressService.save(requestDto.getAddressDto()))
                .thenReturn(accommodationModel.getAddress());
        when(sizeTypeRepository.findById(requestDto.getSizeId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> accommodationService.save(requestDto));

        verify(accommodationMapper, times(1)).toModelWithoutAddressAndTypes(requestDto);
        verify(addressService, times(1)).save(requestDto.getAddressDto());
        verify(sizeTypeRepository, times(1)).findById(requestDto.getSizeId());

        verifyNoMoreInteractions(
                accommodationMapper,
                addressService,
                sizeTypeRepository,
                accommodationTypeRepository,
                amenityTypeRepository,
                accommodationRepository
        );
    }

    @Test
    @DisplayName("Save accommodation with not valid accommodation type")
    void save_saveAccommodationWithNotValidAccommodationType_ThrowException() {
        AccommodationRequestDto requestDto = AccommodationSampleUtil.getRequestDto(1L);
        requestDto.setTypeId(null);
        Accommodation accommodationModel = getAccommodationModel(1L);

        when(accommodationMapper.toModelWithoutAddressAndTypes(requestDto))
                .thenReturn(accommodationModel);
        when(addressService.save(requestDto.getAddressDto()))
                .thenReturn(accommodationModel.getAddress());
        when(sizeTypeRepository.findById(requestDto.getSizeId()))
                .thenReturn(Optional.of(accommodationModel.getSize()));
        when(accommodationTypeRepository.findById(requestDto.getTypeId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> accommodationService.save(requestDto));

        verify(accommodationMapper, times(1)).toModelWithoutAddressAndTypes(requestDto);
        verify(addressService, times(1)).save(requestDto.getAddressDto());
        verify(sizeTypeRepository, times(1)).findById(requestDto.getSizeId());
        verify(accommodationTypeRepository, times(1)).findById(requestDto.getTypeId());

        verifyNoMoreInteractions(
                accommodationMapper,
                addressService,
                sizeTypeRepository,
                accommodationTypeRepository,
                amenityTypeRepository,
                accommodationRepository
        );
    }

    @Test
    @DisplayName("Save accommodation with not valid amenities type")
    void save_saveAccommodationWithNotValidAmenities_ThrowException() {
        AccommodationRequestDto requestDto = AccommodationSampleUtil.getRequestDto(1L);
        requestDto.setAmenityTypeIds(null);
        Accommodation accommodationModel = AccommodationSampleUtil.getAccommodationModel(1L);

        when(accommodationMapper.toModelWithoutAddressAndTypes(requestDto))
                .thenReturn(accommodationModel);
        when(addressService.save(requestDto.getAddressDto()))
                .thenReturn(accommodationModel.getAddress());
        when(sizeTypeRepository.findById(requestDto.getSizeId()))
                .thenReturn(Optional.of(accommodationModel.getSize()));
        when(accommodationTypeRepository.findById(requestDto.getTypeId()))
                .thenReturn(Optional.of(accommodationModel.getType()));
        when(amenityTypeRepository.findAllById(requestDto.getAmenityTypeIds()))
                .thenReturn(List.of());

        assertThrows(EntityNotFoundException.class,
                () -> accommodationService.save(requestDto));

        verify(accommodationMapper, times(1)).toModelWithoutAddressAndTypes(requestDto);
        verify(addressService, times(1)).save(requestDto.getAddressDto());
        verify(sizeTypeRepository, times(1)).findById(requestDto.getSizeId());
        verify(accommodationTypeRepository, times(1)).findById(requestDto.getTypeId());
        verify(amenityTypeRepository, times(1)).findAllById(requestDto.getAmenityTypeIds());

        verifyNoMoreInteractions(
                accommodationMapper,
                addressService,
                sizeTypeRepository,
                accommodationTypeRepository,
                amenityTypeRepository,
                accommodationRepository
        );
    }

    @Test
    @DisplayName("Get accommodation by id with valid data")
    void getById_getAccommodationWithValidData_ReturnAccommodation() {
        Accommodation accommodationModel = AccommodationSampleUtil.getAccommodationModel(1L);
        accommodationModel.setId(1L);
        AccommodationDto responseDto = AccommodationSampleUtil.getAccommodationDto(1L);
        responseDto.setId(1L);

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationModel));
        when(accommodationMapper.toDto(accommodationModel)).thenReturn(responseDto);

        AccommodationDto result = accommodationService.getById(1L);

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(accommodationRepository, times(1)).findById(1L);
        verify(accommodationMapper, times(1)).toDto(accommodationModel);

        verifyNoMoreInteractions(
                accommodationMapper,
                addressService,
                sizeTypeRepository,
                accommodationTypeRepository,
                amenityTypeRepository,
                accommodationRepository
        );
    }

    @Test
    @DisplayName("Get accommodation by id with not valid data")
    void getById_getAccommodationWithNotValidData_ThrowException() {
        Long notValidId = - 1L;
        when(accommodationRepository.findById(notValidId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> accommodationService.getById(notValidId));

        verify(accommodationRepository, times(1)).findById(notValidId);

        verifyNoMoreInteractions(
                accommodationMapper,
                addressService,
                sizeTypeRepository,
                accommodationTypeRepository,
                amenityTypeRepository,
                accommodationRepository
        );
    }

    @Test
    @DisplayName("Search accommodation with valid data")
    void search_searchAccommodationWithValidData_ReturnAccommodations() {
        Pageable pageable = PageRequest.of(0, 10);
        AccommodationSearchDto requestDto = new AccommodationSearchDto();
        Accommodation accommodation1 = getAccommodationModel(1L);
        Accommodation accommodation2 = getAccommodationModel(2L);
        List<Accommodation> accommodations = List.of(accommodation1, accommodation2);
        Page<Accommodation> accommodationPage = new PageImpl<>(accommodations, pageable, 2);

        when(accommodationSpecificationBuilder.build(any(AccommodationSearchDto.class)))
                .thenReturn(mock(Specification.class));
        when(accommodationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(accommodationPage);
        when(accommodationMapper.toDto(accommodation1)).thenReturn(new AccommodationDto());
        when(accommodationMapper.toDto(accommodation2)).thenReturn(new AccommodationDto());

        List<AccommodationDto> result = accommodationService.search(pageable, requestDto);

        assertEquals(2, result.size());

        verify(accommodationSpecificationBuilder).build(requestDto);
        verify(accommodationRepository).findAll(any(Specification.class), eq(pageable));
        verify(accommodationMapper, times(2)).toDto(any(Accommodation.class));
        verifyNoMoreInteractions(
                accommodationSpecificationBuilder,
                accommodationMapper,
                addressService,
                sizeTypeRepository,
                accommodationTypeRepository,
                amenityTypeRepository,
                accommodationRepository
        );
    }

    @Test
    @DisplayName("Find all accommodation with valid data")
    void findAll_findAccommodationWithValidData_ReturnAccommodations() {
        Accommodation accommodation1 = AccommodationSampleUtil.getAccommodationModel(1L);
        accommodation1.setId(1L);
        Accommodation accommodation2 = AccommodationSampleUtil.getAccommodationModel(2L);
        accommodation2.setId(2L);
        AccommodationDto responseDto1 = AccommodationSampleUtil.getAccommodationDto(1L);
        responseDto1.setId(1L);
        AccommodationDto responseDto2 = AccommodationSampleUtil.getAccommodationDto(1L);
        responseDto2.setId(2L);

        Pageable pageable = PageRequest.of(0, 10);

        List<Accommodation> accommodationList = List.of(accommodation1, accommodation2);
        Page<Accommodation> accommodationPage = new PageImpl<>(accommodationList, pageable, 2);

        when(accommodationRepository.findAll(pageable)).thenReturn(accommodationPage);
        when(accommodationMapper.toDto(accommodation1)).thenReturn(responseDto1);
        when(accommodationMapper.toDto(accommodation2)).thenReturn(responseDto2);

        List<AccommodationDto> result = accommodationService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(responseDto1, result.get(0));
        assertEquals(responseDto2, result.get(1));

        verify(accommodationRepository, times(1)).findAll(pageable);
        verify(accommodationMapper, times(1)).toDto(accommodation1);
        verify(accommodationMapper, times(1)).toDto(accommodation2);
        verifyNoMoreInteractions(
                accommodationSpecificationBuilder,
                accommodationMapper,
                addressService,
                sizeTypeRepository,
                accommodationTypeRepository,
                amenityTypeRepository,
                accommodationRepository
        );
    }

    @Test
    @DisplayName("Update accommodation with valid data")
    void update_updateAccommodationWithValidData_ReturnAccommodation() {
        AccommodationRequestDto requestDto = AccommodationSampleUtil.getRequestDto(1L);
        requestDto.setSizeId(2L);
        requestDto.getAddressDto().setCity("UPDATE");

        Accommodation accommodationUpdModel = AccommodationSampleUtil.getAccommodationModel(1L);
        accommodationUpdModel.getAddress().setCity("UPDATE");

        Accommodation accommodationModel = AccommodationSampleUtil.getAccommodationModel(1L);
        AccommodationDto responseDto = AccommodationSampleUtil.getAccommodationDto(1L);

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationModel));
        when(accommodationMapper.toModelWithoutAddressAndTypes(requestDto))
                .thenReturn(accommodationUpdModel);
        when(sizeTypeRepository.findById(requestDto.getSizeId()))
                .thenReturn(Optional.of(accommodationUpdModel.getSize()));
        when(accommodationTypeRepository.findById(requestDto.getTypeId()))
                .thenReturn(Optional.of(accommodationUpdModel.getType()));
        when(amenityTypeRepository.findAllById(requestDto.getAmenityTypeIds()))
                .thenReturn(accommodationUpdModel.getAmenities().stream().toList());
        when(addressMapper.toModel(requestDto.getAddressDto()))
                .thenReturn(accommodationUpdModel.getAddress());
        when(addressRepository.save(accommodationUpdModel.getAddress()))
                .thenReturn(accommodationUpdModel.getAddress());
        when(accommodationRepository.save(accommodationUpdModel)).thenReturn(accommodationModel);
        when(accommodationMapper.toDto(accommodationModel)).thenReturn(responseDto);

        AccommodationDto result = accommodationService.update(1L, requestDto);

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(accommodationRepository, times(1)).findById(1L);
        verify(accommodationMapper, times(1)).toModelWithoutAddressAndTypes(requestDto);
        verify(sizeTypeRepository, times(1)).findById(requestDto.getSizeId());
        verify(accommodationTypeRepository, times(1)).findById(requestDto.getTypeId());
        verify(amenityTypeRepository, times(1)).findAllById(requestDto.getAmenityTypeIds());
        verify(addressMapper, times(1)).toModel(requestDto.getAddressDto());
        verify(addressRepository, times(1)).save(accommodationUpdModel.getAddress());

        verify(accommodationRepository, times(1)).save(accommodationUpdModel);
        verify(accommodationMapper, times(1)).toDto(accommodationModel);

        verifyNoMoreInteractions(
                accommodationMapper,
                addressService,
                sizeTypeRepository,
                accommodationTypeRepository,
                amenityTypeRepository,
                accommodationRepository
        );
    }

    @Test
    @DisplayName("Delete accommodation by id with valid data")
    void deleteById_saveAccommodationWithValidData_ReturnAccommodation() {
        Accommodation accommodationModel = AccommodationSampleUtil.getAccommodationModel(1L);

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodationModel));

        accommodationService.deleteById(1L);

        verify(accommodationRepository, times(1)).findById(1L);
        verify(accommodationRepository, times(1)).deleteById(1L);

        verifyNoMoreInteractions(
                accommodationMapper,
                addressService,
                sizeTypeRepository,
                accommodationTypeRepository,
                amenityTypeRepository,
                accommodationRepository
        );
    }
}
