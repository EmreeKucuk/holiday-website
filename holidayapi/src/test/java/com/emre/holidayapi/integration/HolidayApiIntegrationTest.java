package com.emre.holidayapi.integration;

import com.emre.holidayapi.model.Country;
import com.emre.holidayapi.model.HolidayDefinition;
import com.emre.holidayapi.service.CountryService;
import com.emre.holidayapi.service.HolidayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class HolidayApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HolidayService holidayService;

    @MockitoBean
    private CountryService countryService;

    @Test
    void integrationTest_GetAllCountries_ShouldWork() throws Exception {
        // Given
        Country country = new Country();
        country.setCountryCode("TR");
        country.setCountryName("Turkey");
        
        when(countryService.getAllCountries()).thenReturn(Arrays.asList(country));

        // When & Then
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].countryCode").value("TR"));
    }

    @Test
    void integrationTest_GetAllHolidays_ShouldWork() throws Exception {
        // Given
        HolidayDefinition holiday = new HolidayDefinition();
        holiday.setId(1L);
        holiday.setHolidayDate(LocalDate.now());
        
        when(holidayService.getAllHolidays()).thenReturn(Arrays.asList(holiday));

        // When & Then
        mockMvc.perform(get("/api/holidays"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void integrationTest_CrossOriginSupport_ShouldWork() throws Exception {
        // Given
        when(countryService.getAllCountries()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/countries")
                .header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }
}
