package org.example.bookingapplication.repository.accommodation.specefications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.model.booking.Booking;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.example.bookingapplication.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckDateSpecificationProvider
        implements SpecificationProvider<Accommodation, LocalDate[]> {
    private static final String TYPE_IDS_KEY = "checkDates";
    private static final String PENDING_STATUS = "PENDING";
    private static final String CONFIRMED_STATUS = "CONFIRMED";
    private static final int CHECK_IN_DATE_POSITION = 0;
    private static final int CHECK_OUT_DATE_POSITION = 1;
    private static final String BOOKING_ACCOMMODATION_FIELD = "accommodation";
    private static final String BOOKING_STATUS_FIELD = "status";
    private static final String BOOKING_CHECK_DATES_FIELD = "checkDates";
    private static final String BOOKING_CHECK_IN_DATE_FIELD = "checkInDate";
    private static final String BOOKING_CHECK_OUT_DATE_FIELD = "checkOutDate";
    private static final String BOOKING_ID_FIELD = "id";

    @Override
    public String getKey() {
        return TYPE_IDS_KEY;
    }

    @Override
    public Specification<Accommodation> getSpecification(LocalDate[] params) {
        return (root, query, criteriaBuilder) -> {
            LocalDate checkInDate = params[CHECK_IN_DATE_POSITION];
            LocalDate checkOutDate = params[CHECK_OUT_DATE_POSITION];

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Booking> bookingRoot = subquery.from(Booking.class);
            Join<Booking, Accommodation> bookingAccommodationJoin =
                    bookingRoot.join(BOOKING_ACCOMMODATION_FIELD);
            Join<Booking, BookingStatus> statusJoin = bookingRoot.join(BOOKING_STATUS_FIELD);

            subquery.select(criteriaBuilder.count(bookingRoot.get(BOOKING_ID_FIELD)));

            Predicate overlappingDates = criteriaBuilder.or(
                    criteriaBuilder.between(bookingRoot.get(
                            BOOKING_CHECK_DATES_FIELD).get(BOOKING_CHECK_IN_DATE_FIELD),
                            checkInDate, checkOutDate),
                    criteriaBuilder.between(bookingRoot.get(
                            BOOKING_CHECK_DATES_FIELD).get(BOOKING_CHECK_OUT_DATE_FIELD),
                            checkInDate, checkOutDate),
                    criteriaBuilder.and(
                            criteriaBuilder.lessThanOrEqualTo(bookingRoot.get(
                                    BOOKING_CHECK_DATES_FIELD)
                                    .get(BOOKING_CHECK_IN_DATE_FIELD), checkInDate),
                            criteriaBuilder.greaterThanOrEqualTo(bookingRoot.get(
                                    BOOKING_CHECK_DATES_FIELD)
                                    .get(BOOKING_CHECK_OUT_DATE_FIELD), checkOutDate)
                    )
            );

            Predicate statusIsPendingOrConfirmed = criteriaBuilder.or(
                    criteriaBuilder.equal(statusJoin.get("name"), PENDING_STATUS),
                    criteriaBuilder.equal(statusJoin.get("name"), CONFIRMED_STATUS)
            );

            subquery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(
                                    bookingAccommodationJoin.get(
                                            BOOKING_ID_FIELD), root.get(BOOKING_ID_FIELD)),
                            statusIsPendingOrConfirmed,
                            overlappingDates
                    )
            );

            return criteriaBuilder.equal(subquery, 0L);
        };
    }
}
