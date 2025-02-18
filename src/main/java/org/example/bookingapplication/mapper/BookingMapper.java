package org.example.bookingapplication.mapper;

import java.time.LocalDateTime;
import org.example.bookingapplication.config.MapperConfig;
import org.example.bookingapplication.dto.bookings.request.BookingRequestDto;
import org.example.bookingapplication.dto.bookings.responce.BookingDto;
import org.example.bookingapplication.model.accommodation.Address;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.booking.CheckDate;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = {
        UserMapper.class,
        AccommodationMapper.class,
        CheckDateMapper.class,
        BookingStatusMapper.class})
public interface BookingMapper {
    String DESCRIPTION = "Booking address: %s, %s, %s %s, %s, \n Check-dates: %s, %s";

    BookingDto toDto(Booking booking);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Booking toModelWithoutStatusAndUser(BookingRequestDto requestDto);

    @AfterMapping
    default void setCreatedAtToBooking(@MappingTarget Booking booking,
                                       BookingRequestDto requestDto) {
        LocalDateTime nowDateTime = LocalDateTime.now();
        booking.setCreatedAt(nowDateTime);
    }

    void setUpdateInfoToBooking(@MappingTarget Booking booking, BookingRequestDto requestDto);

    default String getBookingDescription(Booking booking) {
        Address address = booking.getAccommodation().getAddress();
        CheckDate checkDate = booking.getCheckDates();
        return DESCRIPTION.formatted(address.getCountry(), address.getState(), address.getCity(),
                address.getStreet(), address.getHouseNumber(),
                checkDate.getCheckInDate(), checkDate.getCheckOutDate());
    }

}
