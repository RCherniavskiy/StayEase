package org.example.bookingapplication.repository.accommodation.specefications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.model.accommodation.AmenityType;
import org.example.bookingapplication.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AmenityTypeSpecificationProvider
        implements SpecificationProvider<Accommodation, Long[]> {
    private static final String TYPE_IDS_KEY = "amenityTypes";
    private static final String AMENITY_TYPE_FIELD_NAME = "amenities";

    @Override
    public String getKey() {
        return TYPE_IDS_KEY;
    }

    @Override
    public Specification<Accommodation> getSpecification(Long[] params) {
        return (root, query, criteriaBuilder) -> {
            Join<Accommodation, AmenityType> amenityJoin = root.join(AMENITY_TYPE_FIELD_NAME);
            List<Predicate> predicates = new ArrayList<>();
            for (Long amenityTypeId : params) {
                predicates.add(criteriaBuilder.equal(amenityJoin.get("id"), amenityTypeId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
