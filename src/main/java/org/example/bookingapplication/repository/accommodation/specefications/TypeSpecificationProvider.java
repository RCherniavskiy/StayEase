package org.example.bookingapplication.repository.accommodation.specefications;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TypeSpecificationProvider implements SpecificationProvider<Accommodation, Long[]> {
    private static final String TYPE_IDS_KEY = "types";
    private static final String TYPE_FIELD_NAME = "type";

    @Override
    public String getKey() {
        return TYPE_IDS_KEY;
    }

    @Override
    public Specification<Accommodation> getSpecification(Long[] params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Long typeId : params) {
                predicates.add(criteriaBuilder.equal(root.get(TYPE_FIELD_NAME).get("id"), typeId));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
