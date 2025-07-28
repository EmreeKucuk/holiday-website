package com.emre.holidayapi.service;

import com.emre.holidayapi.model.HolidayDefinition;
import com.emre.holidayapi.model.HolidayTemplate;
import com.emre.holidayapi.repository.HolidayDefinitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @Mock
    private HolidayDefinitionRepository repository;

    @InjectMocks
    private HolidayService holidayService;

    private HolidayDefinition holidayDefinition;
    private HolidayTemplate holidayTemplate;

    @BeforeEach
    void setUp() {
        holidayTemplate = new HolidayTemplate();
        holidayTemplate.setId(1L);
        holidayTemplate.setDefaultName("New Year");
        holidayTemplate.setCode("new_year");
        holidayTemplate.setType("Official");

        holidayDefinition = new HolidayDefinition();
        holidayDefinition.setId(1L);
        holidayDefinition.setTemplate(holidayTemplate);
        holidayDefinition.setHolidayDate(LocalDate.of(2025, 1, 1));
    }

    @Test
    void getAllHolidays_ShouldReturnAllHolidays() {
        // Given
        List<HolidayDefinition> expectedHolidays = Arrays.asList(holidayDefinition);
        when(repository.findAll()).thenReturn(expectedHolidays);

        // When
        List<HolidayDefinition> actualHolidays = holidayService.getAllHolidays();

        // Then
        assertThat(actualHolidays).isEqualTo(expectedHolidays);
        verify(repository).findAll();
    }

    @Test
    void getHolidaysByCountry_ShouldReturnHolidaysForCountry() {
        // Given
        String countryCode = "TR";
        List<HolidayDefinition> expectedHolidays = Arrays.asList(holidayDefinition);
        when(repository.findByCountryCode(countryCode)).thenReturn(expectedHolidays);

        // When
        List<HolidayDefinition> actualHolidays = holidayService.getHolidaysByCountry(countryCode);

        // Then
        assertThat(actualHolidays).isEqualTo(expectedHolidays);
        verify(repository).findByCountryCode(countryCode);
    }

    @Test
    void getHolidaysByCountryAndYear_ShouldReturnHolidaysForCountryAndYear() {
        // Given
        String countryCode = "TR";
        int year = 2025;
        List<HolidayDefinition> expectedHolidays = Arrays.asList(holidayDefinition);
        when(repository.findByCountryCodeAndYear(countryCode, year)).thenReturn(expectedHolidays);

        // When
        List<HolidayDefinition> actualHolidays = holidayService.getHolidaysByCountryAndYear(countryCode, year);

        // Then
        assertThat(actualHolidays).isEqualTo(expectedHolidays);
        verify(repository).findByCountryCodeAndYear(countryCode, year);
    }

    @Test
    void getHolidayTypes_ShouldReturnDistinctTypes() {
        // Given
        List<String> expectedTypes = Arrays.asList("Official", "Religious", "Cultural");
        when(repository.findDistinctTypes()).thenReturn(expectedTypes);

        // When
        List<String> actualTypes = holidayService.getHolidayTypes();

        // Then
        assertThat(actualTypes).isEqualTo(expectedTypes);
        verify(repository).findDistinctTypes();
    }

    @Test
    void getWorkDaysBetweenDates_ShouldCalculateWorkDaysCorrectly() {
        // Given - Monday to Friday (5 work days)
        String start = "2025-01-06"; // Monday
        String end = "2025-01-10";   // Friday

        // When
        int workDays = holidayService.getWorkDaysBetweenDates(start, end);

        // Then
        assertThat(workDays).isEqualTo(5);
    }

    @Test
    void getWorkDaysBetweenDates_ShouldExcludeWeekends() {
        // Given - Monday to Sunday (5 work days, excluding Saturday and Sunday)
        String start = "2025-01-06"; // Monday
        String end = "2025-01-12";   // Sunday

        // When
        int workDays = holidayService.getWorkDaysBetweenDates(start, end);

        // Then
        assertThat(workDays).isEqualTo(5);
    }

    @Test
    void getHolidayDetails_WhenHolidayExists_ShouldReturnHoliday() {
        // Given
        Long holidayId = 1L;
        when(repository.findById(holidayId)).thenReturn(Optional.of(holidayDefinition));

        // When
        HolidayDefinition actualHoliday = holidayService.getHolidayDetails(holidayId);

        // Then
        assertThat(actualHoliday).isEqualTo(holidayDefinition);
        verify(repository).findById(holidayId);
    }

    @Test
    void getHolidayDetails_WhenHolidayDoesNotExist_ShouldReturnNull() {
        // Given
        Long holidayId = 999L;
        when(repository.findById(holidayId)).thenReturn(Optional.empty());

        // When
        HolidayDefinition actualHoliday = holidayService.getHolidayDetails(holidayId);

        // Then
        assertThat(actualHoliday).isNull();
        verify(repository).findById(holidayId);
    }

    @Test
    void createHoliday_ShouldSaveAndReturnHoliday() {
        // Given
        when(repository.save(holidayDefinition)).thenReturn(holidayDefinition);

        // When
        HolidayDefinition actualHoliday = holidayService.createHoliday(holidayDefinition);

        // Then
        assertThat(actualHoliday).isEqualTo(holidayDefinition);
        verify(repository).save(holidayDefinition);
    }

    @Test
    void updateHoliday_ShouldSetIdAndSaveHoliday() {
        // Given
        Long holidayId = 1L;
        HolidayDefinition updatedHoliday = new HolidayDefinition();
        updatedHoliday.setTemplate(holidayTemplate);
        updatedHoliday.setHolidayDate(LocalDate.of(2025, 12, 25));

        when(repository.save(any(HolidayDefinition.class))).thenReturn(updatedHoliday);

        // When
        HolidayDefinition actualHoliday = holidayService.updateHoliday(holidayId, updatedHoliday);

        // Then
        assertThat(actualHoliday.getId()).isEqualTo(holidayId);
        verify(repository).save(updatedHoliday);
    }

    @Test
    void deleteHoliday_ShouldCallRepositoryDeleteById() {
        // Given
        Long holidayId = 1L;

        // When
        holidayService.deleteHoliday(holidayId);

        // Then
        verify(repository).deleteById(holidayId);
    }

    @Test
    void getHolidaysByDate_ShouldReturnHolidaysForSpecificDate() {
        // Given
        LocalDate date = LocalDate.of(2025, 1, 1);
        List<HolidayDefinition> expectedHolidays = Arrays.asList(holidayDefinition);
        when(repository.findByHolidayDate(date)).thenReturn(expectedHolidays);

        // When
        List<HolidayDefinition> actualHolidays = holidayService.getHolidaysByDate(date);

        // Then
        assertThat(actualHolidays).isEqualTo(expectedHolidays);
        verify(repository).findByHolidayDate(date);
    }

    @Test
    void getHolidaysInRange_ShouldReturnHolidaysInDateRange() {
        // Given
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        List<HolidayDefinition> expectedHolidays = Arrays.asList(holidayDefinition);
        when(repository.findByHolidayDateBetween(start, end)).thenReturn(expectedHolidays);

        // When
        List<HolidayDefinition> actualHolidays = holidayService.getHolidaysInRange(start, end);

        // Then
        assertThat(actualHolidays).isEqualTo(expectedHolidays);
        verify(repository).findByHolidayDateBetween(start, end);
    }

    @Test
    void getHolidaysByCountryAndDateRange_ShouldReturnHolidaysForCountryInRange() {
        // Given
        String countryCode = "TR";
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        List<HolidayDefinition> expectedHolidays = Arrays.asList(holidayDefinition);
        when(repository.findByCountryCodeAndDateRange(countryCode, start, end)).thenReturn(expectedHolidays);

        // When
        List<HolidayDefinition> actualHolidays = holidayService.getHolidaysByCountryAndDateRange(countryCode, start, end);

        // Then
        assertThat(actualHolidays).isEqualTo(expectedHolidays);
        verify(repository).findByCountryCodeAndDateRange(countryCode, start, end);
    }

    @Test
    void getHolidaysByCountryDateRangeAndAudience_ShouldReturnFilteredHolidays() {
        // Given
        String countryCode = "TR";
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        String audienceCode = "general";
        List<HolidayDefinition> expectedHolidays = Arrays.asList(holidayDefinition);
        when(repository.findByCountryCodeDateRangeAndAudience(countryCode, start, end, audienceCode)).thenReturn(expectedHolidays);

        // When
        List<HolidayDefinition> actualHolidays = holidayService.getHolidaysByCountryDateRangeAndAudience(countryCode, start, end, audienceCode);

        // Then
        assertThat(actualHolidays).isEqualTo(expectedHolidays);
        verify(repository).findByCountryCodeDateRangeAndAudience(countryCode, start, end, audienceCode);
    }

    @Test
    void getHolidaysByDateAndCountry_ShouldReturnHolidaysForDateAndCountry() {
        // Given
        LocalDate date = LocalDate.of(2025, 1, 1);
        String countryCode = "TR";
        List<HolidayDefinition> expectedHolidays = Arrays.asList(holidayDefinition);
        when(repository.findByHolidayDateAndCountryCode(date, countryCode)).thenReturn(expectedHolidays);

        // When
        List<HolidayDefinition> actualHolidays = holidayService.getHolidaysByDate(date, countryCode);

        // Then
        assertThat(actualHolidays).isEqualTo(expectedHolidays);
        verify(repository).findByHolidayDateAndCountryCode(date, countryCode);
    }

    @Test
    void getHolidaysByDateRange_ShouldReturnHolidaysForCountryInRange() {
        // Given
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        String countryCode = "TR";
        List<HolidayDefinition> expectedHolidays = Arrays.asList(holidayDefinition);
        when(repository.findByCountryCodeAndDateRange(countryCode, start, end)).thenReturn(expectedHolidays);

        // When
        List<HolidayDefinition> actualHolidays = holidayService.getHolidaysByDateRange(start, end, countryCode);

        // Then
        assertThat(actualHolidays).isEqualTo(expectedHolidays);
        verify(repository).findByCountryCodeAndDateRange(countryCode, start, end);
    }

    @Test
    void getHolidaysByDateRangeAndAudience_ShouldReturnFilteredHolidays() {
        // Given
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        String countryCode = "TR";
        String audienceCode = "general";
        List<HolidayDefinition> expectedHolidays = Arrays.asList(holidayDefinition);
        when(repository.findByCountryCodeDateRangeAndAudience(countryCode, start, end, audienceCode)).thenReturn(expectedHolidays);

        // When
        List<HolidayDefinition> actualHolidays = holidayService.getHolidaysByDateRangeAndAudience(start, end, countryCode, audienceCode);

        // Then
        assertThat(actualHolidays).isEqualTo(expectedHolidays);
        verify(repository).findByCountryCodeDateRangeAndAudience(countryCode, start, end, audienceCode);
    }

    @Test
    void addHolidayCondition_ShouldReturnNull() {
        // Given
        Long id = 1L;
        Object condition = new Object();

        // When
        Object result = holidayService.addHolidayCondition(id, condition);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void updateHolidayCondition_ShouldReturnNull() {
        // Given
        Long conditionId = 1L;
        Object condition = new Object();

        // When
        Object result = holidayService.updateHolidayCondition(conditionId, condition);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void deleteHolidayCondition_ShouldExecuteWithoutException() {
        // Given
        Long conditionId = 1L;

        // When & Then - should not throw exception
        holidayService.deleteHolidayCondition(conditionId);
    }

    @Test
    void addHolidaySpec_ShouldReturnNull() {
        // Given
        Long id = 1L;
        Object spec = new Object();

        // When
        Object result = holidayService.addHolidaySpec(id, spec);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void updateHolidaySpec_ShouldReturnNull() {
        // Given
        Long specId = 1L;
        Object spec = new Object();

        // When
        Object result = holidayService.updateHolidaySpec(specId, spec);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void deleteHolidaySpec_ShouldExecuteWithoutException() {
        // Given
        Long specId = 1L;

        // When & Then - should not throw exception
        holidayService.deleteHolidaySpec(specId);
    }
}
