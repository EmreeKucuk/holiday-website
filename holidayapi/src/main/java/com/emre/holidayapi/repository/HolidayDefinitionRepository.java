package com.emre.holidayapi.repository;

import com.emre.holidayapi.model.HolidayDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface HolidayDefinitionRepository extends JpaRepository<HolidayDefinition, Long> {
    List<HolidayDefinition> findByTemplate_Code(String code);

    // Example methods, adjust according to your model relationships

    @Query("SELECT h FROM HolidayDefinition h JOIN h.template t JOIN CountryHoliday ch ON ch.template = t WHERE ch.country.countryCode = :countryCode")
    List<HolidayDefinition> findByCountryCode(String countryCode);

    @Query("SELECT h FROM HolidayDefinition h JOIN h.template t JOIN CountryHoliday ch ON ch.template = t WHERE ch.country.countryCode = :countryCode AND FUNCTION('YEAR', h.holidayDate) = :year")
    List<HolidayDefinition> findByCountryCodeAndYear(String countryCode, int year);

    @Query("SELECT DISTINCT t.type FROM HolidayTemplate t")
    List<String> findDistinctTypes();

    List<HolidayDefinition> findByHolidayDate(LocalDate date);

    List<HolidayDefinition> findByHolidayDateBetween(LocalDate start, LocalDate end);
}
