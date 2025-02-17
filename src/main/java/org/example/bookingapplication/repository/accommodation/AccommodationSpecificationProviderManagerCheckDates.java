package org.example.bookingapplication.repository.accommodation;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.repository.SpecificationProvider;
import org.example.bookingapplication.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccommodationSpecificationProviderManagerCheckDates
        implements SpecificationProviderManager<Accommodation, LocalDate[]> {
    private final List<SpecificationProvider<Accommodation, LocalDate[]>>
            specificationProvidersCheckDates;

    @Override
    public SpecificationProvider<Accommodation, LocalDate[]> getSpecificationProvider(String key) {
        return specificationProvidersCheckDates.stream()
            .filter(spec -> spec.getKey().equals(key))
            .findFirst()
            .orElseThrow(
                    () -> new RuntimeException("Can't find correct "
                        + "accommodationSpecificationProvider where key = " + key));
    }
}
