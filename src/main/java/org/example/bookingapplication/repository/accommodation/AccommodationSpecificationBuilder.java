package org.example.bookingapplication.repository.accommodation;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.accommodations.request.AccommodationSearchDto;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.repository.SpecificationBuilder;
import org.example.bookingapplication.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccommodationSpecificationBuilder implements SpecificationBuilder<Accommodation> {
    private final SpecificationProviderManager<Accommodation, Long[]>
            specificationProviderManagerLongArr;
    private final SpecificationProviderManager<Accommodation, BigDecimal>
            specificationProviderManagerBigDec;
    private final SpecificationProviderManager<Accommodation, LocalDate[]>
            specificationProviderManagerCheckDates;

    @Override
    public Specification<Accommodation> build(AccommodationSearchDto requestDto) {
        Specification<Accommodation> specs = Specification.where(null);

        if (requestDto.getTypeIds() != null
                && requestDto.getTypeIds().length > 0) {
            specs = specs.and(
                    specificationProviderManagerLongArr.getSpecificationProvider("types")
                    .getSpecification(requestDto.getTypeIds())
            );
        }
        if (requestDto.getSizeTypeIds() != null
                && requestDto.getSizeTypeIds().length > 0) {
            specs = specs.and(
                    specificationProviderManagerLongArr.getSpecificationProvider("sizeTypes")
                            .getSpecification(requestDto.getSizeTypeIds())
            );
        }
        if (requestDto.getAmenityTypeIds() != null
                && requestDto.getAmenityTypeIds().length > 0) {
            specs = specs.and(
                    specificationProviderManagerLongArr.getSpecificationProvider("amenityTypes")
                    .getSpecification(requestDto.getAmenityTypeIds())
            );
        }
        if (requestDto.getDailyRateMin() != null) {
            specs = specs.and(
                specificationProviderManagerBigDec.getSpecificationProvider("dailyRateMin")
                    .getSpecification(requestDto.getDailyRateMin())
            );
        }
        if (requestDto.getDailyRateMax() != null) {
            specs = specs.and(
                    specificationProviderManagerBigDec.getSpecificationProvider("dailyRateMax")
                    .getSpecification(requestDto.getDailyRateMax())
            );
        }
        if (requestDto.getCheckDates() != null) {
            specs = specs.and(
                    specificationProviderManagerCheckDates.getSpecificationProvider("checkDates")
                            .getSpecification(requestDto.getCheckDates())
            );
        }
        return specs;
    }
}
