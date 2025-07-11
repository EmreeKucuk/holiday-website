package com.emre.holidayapi.repository;

import com.emre.holidayapi.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, String> {
}