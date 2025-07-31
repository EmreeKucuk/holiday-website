package com.emre.holidayapi.controller;

import com.emre.holidayapi.model.Country;
import com.emre.holidayapi.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CountryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CountryService countryService;

    private Country country1;
    private Country country2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        CountryController countryController = new CountryController(countryService);
        mockMvc = MockMvcBuilders.standaloneSetup(countryController).build();
        country1 = new Country();
        country1.setCountryCode("TR");
        country1.setCountryName("Turkey");

        country2 = new Country();
        country2.setCountryCode("US");
        country2.setCountryName("United States");
    }

    @Test
    void getAllCountries_ShouldReturnAllCountries() throws Exception {
        // Given
        List<Country> countries = Arrays.asList(country1, country2);
        when(countryService.getAllCountries()).thenReturn(countries);

        // When & Then
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].countryCode", is("TR")))
                .andExpect(jsonPath("$[0].name", is("Turkey")))
                .andExpect(jsonPath("$[1].countryCode", is("US")))
                .andExpect(jsonPath("$[1].name", is("United States")));
    }

    @Test
    void getAllCountries_WhenNoCountries_ShouldReturnEmptyList() throws Exception {
        // Given
        when(countryService.getAllCountries()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllCountries_ShouldReturnCorrectContentType() throws Exception {
        // Given
        List<Country> countries = Arrays.asList(country1);
        when(countryService.getAllCountries()).thenReturn(countries);

        // When & Then
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}
