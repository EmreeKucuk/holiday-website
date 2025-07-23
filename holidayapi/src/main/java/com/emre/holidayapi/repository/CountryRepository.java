package com.emre.holidayapi.repository;

import com.emre.holidayapi.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, String> {
    Optional<Country> findByCountryCode(String countryCode);
}