package org.example.bookingapplication.repository.accommodation.specefications;

import java.math.BigDecimal;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DailyRateMaxSpecificationProvider
        implements SpecificationProvider<Accommodation, BigDecimal> {
    private static final String DAILY_RATE_MAX_KEY = "dailyRateMax";
    private static final String DAILY_RATE_FIELD_NAME = "dailyRate";
    private static final BigDecimal MIN_VALUE = BigDecimal.valueOf(0);

    @Override
    public String getKey() {
        return DAILY_RATE_MAX_KEY;
    }

    @Override
    public Specification<Accommodation> getSpecification(BigDecimal params) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .between(root.get(DAILY_RATE_FIELD_NAME), MIN_VALUE, params);
    }
}
