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
    private static final int CHECK_IN_DATE_POSITION = 0;
    private static final int CHECK_OUT_DATE_POSITION = 1;

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
                    bookingRoot.join("accommodation");
            Join<Booking, BookingStatus> statusJoin = bookingRoot.join("status");

            subquery.select(criteriaBuilder.count(bookingRoot.get("id")));

            Predicate overlappingDates = criteriaBuilder.or(
                    criteriaBuilder.between(bookingRoot.get("checkDates").get("checkInDate"),
                            checkInDate, checkOutDate),
                    criteriaBuilder.between(bookingRoot.get("checkDates").get("checkOutDate"),
                            checkInDate, checkOutDate),
                    criteriaBuilder.and(
                            criteriaBuilder.lessThanOrEqualTo(bookingRoot.get("checkDates")
                                    .get("checkInDate"), checkInDate),
                            criteriaBuilder.greaterThanOrEqualTo(bookingRoot.get("checkDates")
                                    .get("checkOutDate"), checkOutDate)
                    )
            );

            Predicate statusIsPendingOrConfirmed = criteriaBuilder.or(
                    criteriaBuilder.equal(statusJoin.get("name"), "PENDING"),
                    criteriaBuilder.equal(statusJoin.get("name"), "CONFIRMED")
            );

            subquery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(
                                    bookingAccommodationJoin.get("id"), root.get("id")),
                            statusIsPendingOrConfirmed,
                            overlappingDates
                    )
            );

            return criteriaBuilder.equal(subquery, 0L);
        };
    }
}
