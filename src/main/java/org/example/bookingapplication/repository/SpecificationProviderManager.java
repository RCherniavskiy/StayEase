package org.example.bookingapplication.repository;

public interface SpecificationProviderManager<T, D> {
    SpecificationProvider<T, D> getSpecificationProvider(String key);
}
