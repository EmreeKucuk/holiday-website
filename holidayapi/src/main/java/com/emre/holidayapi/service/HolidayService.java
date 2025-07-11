package com.emre.holidayapi.service;

import com.emre.holidayapi.model.HolidayDefinition;
import com.emre.holidayapi.repository.HolidayDefinitionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HolidayService {
    private final HolidayDefinitionRepository repository;

    public HolidayService(HolidayDefinitionRepository repository) {
        this.repository = repository;
    }

    public List<HolidayDefinition> getAllHolidays() {
        return repository.findAll();
    }

    public List<HolidayDefinition> getHolidaysByCountry(String countryCode) {
        // Implement this based on your model (example assumes a countryCode field or join)
        return repository.findByCountryCode(countryCode);
    }

    public List<HolidayDefinition> getHolidaysByCountryAndYear(String countryCode, int year) {
        // Implement this based on your model (example assumes a method exists)
        return repository.findByCountryCodeAndYear(countryCode, year);
    }

    public List<String> getHolidayTypes() {
        // Example: fetch distinct types from templates
        return repository.findDistinctTypes();
    }

    public int getWorkDaysBetweenDates(String start, String end) {
        // Example: count days between two dates, excluding weekends
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        int workDays = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (date.getDayOfWeek().getValue() < 6) { // 1=Monday, 7=Sunday
                workDays++;
            }
        }
        return workDays;
    }

    public HolidayDefinition getHolidayDetails(Long id) {
        return repository.findById(id).orElse(null);
    }

    public HolidayDefinition createHoliday(HolidayDefinition holiday) {
        return repository.save(holiday);
    }

    public HolidayDefinition updateHoliday(Long id, HolidayDefinition holiday) {
        holiday.setId(id);
        return repository.save(holiday);
    }

    public void deleteHoliday(Long id) {
        repository.deleteById(id);
    }

    public List<HolidayDefinition> getHolidaysByDate(LocalDate date) {
        return repository.findByHolidayDate(date);
    }

    public List<HolidayDefinition> getHolidaysInRange(LocalDate start, LocalDate end) {
        return repository.findByHolidayDateBetween(start, end);
    }

    public List<HolidayDefinition> getHolidaysByCountryAndDateRange(String countryCode, LocalDate start, LocalDate end) {
        return repository.findByCountryCodeAndDateRange(countryCode, start, end);
    }

    // --- Placeholder implementations for conditions/specs ---

    public Object addHolidayCondition(Long id, Object condition) {
        // Implement logic
        return null;
    }

    public Object updateHolidayCondition(Long conditionId, Object condition) {
        // Implement logic
        return null;
    }

    public void deleteHolidayCondition(Long conditionId) {
        // Implement logic
    }

    public Object addHolidaySpec(Long id, Object spec) {
        // Implement logic
        return null;
    }

    public Object updateHolidaySpec(Long specId, Object spec) {
        // Implement logic
        return null;
    }

    public void deleteHolidaySpec(Long specId) {
        // Implement logic
    }
}