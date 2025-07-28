package com.emre.holidayapi.service;

import com.emre.holidayapi.model.Country;
import com.emre.holidayapi.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository repository;

    @InjectMocks
    private CountryService countryService;

    private Country country1;
    private Country country2;

    @BeforeEach
    void setUp() {
        country1 = new Country();
        country1.setCountryCode("TR");
        country1.setCountryName("Turkey");

        country2 = new Country();
        country2.setCountryCode("US");
        country2.setCountryName("United States");
    }

    @Test
    void getAllCountries_ShouldReturnAllCountries() {
        // Given
        List<Country> expectedCountries = Arrays.asList(country1, country2);
        when(repository.findAll()).thenReturn(expectedCountries);

        // When
        List<Country> actualCountries = countryService.getAllCountries();

        // Then
        assertThat(actualCountries)
                .hasSize(2)
                .containsExactly(country1, country2);
        verify(repository).findAll();
    }

    @Test
    void getAllCountries_WhenNoCountries_ShouldReturnEmptyList() {
        // Given
        when(repository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Country> actualCountries = countryService.getAllCountries();

        // Then
        assertThat(actualCountries).isEmpty();
        verify(repository).findAll();
    }

    @Test
    void getAllCountries_ShouldCallRepositoryOnce() {
        // Given
        List<Country> expectedCountries = Arrays.asList(country1);
        when(repository.findAll()).thenReturn(expectedCountries);

        // When
        countryService.getAllCountries();

        // Then
        verify(repository, times(1)).findAll();
    }
}
