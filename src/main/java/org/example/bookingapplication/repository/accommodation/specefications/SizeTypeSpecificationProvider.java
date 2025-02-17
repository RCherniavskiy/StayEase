package org.example.bookingapplication.repository.accommodation.specefications;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SizeTypeSpecificationProvider implements SpecificationProvider<Accommodation, Long[]> {
    private static final String SIZE_TYPE_IDS_KEY = "sizeTypes";
    private static final String SIZE_TYPE_FIELD_NAME = "size";

    @Override
    public String getKey() {
        return SIZE_TYPE_IDS_KEY;
    }

    @Override
    public Specification<Accommodation> getSpecification(Long[] params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Long sizeTypeId : params) {
                predicates.add(criteriaBuilder.equal(root.get(SIZE_TYPE_FIELD_NAME).get("id"),
                        sizeTypeId));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
