package org.example.bookingapplication.repository.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT COUNT(b) FROM Booking b "
            + "WHERE b.accommodation.id = :accommodationId "
            + "AND NOT b.status.name = 'CANCELED' "
            + "AND NOT b.status.name = 'EXPIRED' "
            + "AND ("
            + "(:checkInDate BETWEEN b.checkDates.checkInDate AND b.checkDates.checkOutDate "
            + "OR :checkOutDate BETWEEN b.checkDates.checkInDate AND b.checkDates.checkOutDate) "
            + "OR (b.checkDates.checkInDate BETWEEN :checkInDate AND :checkOutDate "
            + "OR b.checkDates.checkOutDate BETWEEN :checkInDate AND :checkOutDate))")
    Long isDatesAvailableForAccommodation(@Param("accommodationId") Long accommodationId,
                                             @Param("checkInDate") LocalDate checkInDate,
                                             @Param("checkOutDate") LocalDate checkOutDate);

    List<Booking> findAllByUser_Email(String email, Pageable pageable);

    @Query("select b from Booking b "
            + "where b.checkDates.checkInDate = :checkInDate and b.status.name = :statusName")
    List<Booking> findAllByCheckInDate(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("statusName") BookingStatus.BookingStatusName statusName);

    @Query("select b from Booking b "
            + "where b.checkDates.checkOutDate = :checkOutDate "
            + "and b.status.name = :statusName")
    List<Booking> findAllByCheckOutDate(
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("statusName") BookingStatus.BookingStatusName statusName);

    @Query("select b from Booking b "
            + "where b.createdAt < :dateTime "
            + "and b.status.name = :statusName")
    List<Booking> findAllByCreatedAtAndStatus(
            @Param("dateTime") LocalDateTime checkOutDate,
            @Param("statusName") BookingStatus.BookingStatusName statusName);
}
