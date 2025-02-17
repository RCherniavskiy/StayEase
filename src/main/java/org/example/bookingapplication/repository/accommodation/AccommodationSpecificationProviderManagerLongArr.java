package org.example.bookingapplication.repository.accommodation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.model.accommodation.Accommodation;
import org.example.bookingapplication.repository.SpecificationProvider;
import org.example.bookingapplication.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccommodationSpecificationProviderManagerLongArr
        implements SpecificationProviderManager<Accommodation, Long[]> {
    private final List<SpecificationProvider<Accommodation, Long[]>> specificationProvidersLongArr;

    @Override
    public SpecificationProvider<Accommodation, Long[]> getSpecificationProvider(String key) {
        return specificationProvidersLongArr.stream()
            .filter(spec -> spec.getKey().equals(key))
            .findFirst()
            .orElseThrow(
                    () -> new RuntimeException("Can't find correct "
                        + "accommodationSpecificationProvider where key = " + key));
    }
}
