package com.emre.holidayapi.controller;

import com.emre.holidayapi.dto.AudienceDto;
import com.emre.holidayapi.model.*;
import com.emre.holidayapi.service.HolidayService;
import com.emre.holidayapi.service.AudienceService;
import com.emre.holidayapi.repository.TranslationRepository;
import com.emre.holidayapi.repository.HolidayTemplateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HolidayControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HolidayService holidayService;

    @Mock
    private AudienceService audienceService;

    @Mock
    private TranslationRepository translationRepository;

    @Mock
    private HolidayTemplateRepository holidayTemplateRepository;

    private ObjectMapper objectMapper;

    private HolidayDefinition holidayDefinition;
    private HolidayTemplate holidayTemplate;
    private Audience audience;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        HolidayController holidayController = new HolidayController(holidayService, audienceService, translationRepository, holidayTemplateRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(holidayController).build();
        
        // Configure ObjectMapper for LocalDate serialization
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        holidayTemplate = new HolidayTemplate();
        holidayTemplate.setId(1L);
        holidayTemplate.setCode("new_year");
        holidayTemplate.setDefaultName("New Year");
        holidayTemplate.setType("Official");

        holidayDefinition = new HolidayDefinition();
        holidayDefinition.setId(1L);
        holidayDefinition.setTemplate(holidayTemplate);
        holidayDefinition.setHolidayDate(LocalDate.of(2025, 1, 1));

        audience = new Audience();
        audience.setCode("general");
        audience.setAudienceName("General Public");
    }

    @Test
    void getAllHolidays_ShouldReturnAllHolidays() throws Exception {
        // Given
        List<HolidayDefinition> holidays = Arrays.asList(holidayDefinition);
        when(holidayService.getAllHolidays()).thenReturn(holidays);

        // When & Then
        mockMvc.perform(get("/api/holidays"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getHolidaysByCountry_ShouldReturnHolidaysForCountry() throws Exception {
        // Given
        String countryCode = "TR";
        List<HolidayDefinition> holidays = Arrays.asList(holidayDefinition);
        when(holidayService.getHolidaysByCountry(countryCode)).thenReturn(holidays);

        // When & Then
        mockMvc.perform(get("/api/holidays/country/{countryCode}", countryCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].countryCode", is(countryCode)))
                .andExpect(jsonPath("$[0].type", is("Official")));
    }

    @Test
    void getHolidaysByCountry_WithLanguageParameter_ShouldReturnTranslatedHolidays() throws Exception {
        // Given
        String countryCode = "TR";
        String language = "tr";
        List<HolidayDefinition> holidays = Arrays.asList(holidayDefinition);
        when(holidayService.getHolidaysByCountry(countryCode)).thenReturn(holidays);

        // When & Then
        mockMvc.perform(get("/api/holidays/country/{countryCode}", countryCode)
                .param("language", language))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].countryCode", is(countryCode)));
    }

    @Test
    void getHolidaysByCountryAndYear_ShouldReturnHolidaysForCountryAndYear() throws Exception {
        // Given
        String countryCode = "TR";
        int year = 2025;
        List<HolidayDefinition> holidays = Arrays.asList(holidayDefinition);
        when(holidayService.getHolidaysByCountryAndYear(countryCode, year)).thenReturn(holidays);

        // When & Then
        mockMvc.perform(get("/api/holidays/country/{countryCode}/year/{year}", countryCode, year))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getHolidayTypes_ShouldReturnAllHolidayTypes() throws Exception {
        // Given
        List<String> types = Arrays.asList("Official", "Religious", "Cultural");
        when(holidayService.getHolidayTypes()).thenReturn(types);

        // When & Then
        mockMvc.perform(get("/api/holidays/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Official")))
                .andExpect(jsonPath("$[1]", is("Religious")))
                .andExpect(jsonPath("$[2]", is("Cultural")));
    }

    @Test
    void getTranslatedHolidayTypes_WithEnglish_ShouldReturnEnglishTypes() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/holidays/types/translated")
                .param("language", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.official", is("Official Holiday")))
                .andExpect(jsonPath("$.religious", is("Religious Holiday")))
                .andExpect(jsonPath("$.cultural", is("Cultural Holiday")));
    }

    @Test
    void getTranslatedHolidayTypes_WithTurkish_ShouldReturnTurkishTypes() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/holidays/types/translated")
                .param("language", "tr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.official", is("Resmi Tatil")))
                .andExpect(jsonPath("$.religious", is("Dini Tatil")))
                .andExpect(jsonPath("$.cultural", is("Kültürel Tatil")));
    }

    @Test
    void getWorkDaysBetweenDates_ShouldReturnWorkDaysCount() throws Exception {
        // Given
        String start = "2025-01-01";
        String end = "2025-01-07";
        int workDays = 5;
        when(holidayService.getWorkDaysBetweenDates(start, end)).thenReturn(workDays);

        // When & Then
        mockMvc.perform(get("/api/holidays/workdays")
                .param("start", start)
                .param("end", end))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void getHolidayDetails_ShouldReturnHolidayById() throws Exception {
        // Given
        Long holidayId = 1L;
        when(holidayService.getHolidayDetails(holidayId)).thenReturn(holidayDefinition);

        // When & Then
        mockMvc.perform(get("/api/holidays/{id}", holidayId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void createHoliday_ShouldCreateAndReturnHoliday() throws Exception {
        // Given
        when(holidayService.createHoliday(any(HolidayDefinition.class))).thenReturn(holidayDefinition);

        // When & Then
        mockMvc.perform(post("/api/holidays")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(holidayDefinition)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void updateHoliday_ShouldUpdateAndReturnHoliday() throws Exception {
        // Given
        Long holidayId = 1L;
        when(holidayService.updateHoliday(any(Long.class), any(HolidayDefinition.class))).thenReturn(holidayDefinition);

        // When & Then
        mockMvc.perform(put("/api/holidays/{id}", holidayId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(holidayDefinition)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteHoliday_ShouldDeleteHoliday() throws Exception {
        // Given
        Long holidayId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/holidays/{id}", holidayId))
                .andExpect(status().isOk());
    }

    @Test
    void getAudiences_ShouldReturnAllAudiences() throws Exception {
        // Given
        List<Audience> audiences = Arrays.asList(audience);
        when(audienceService.getAllAudiences()).thenReturn(audiences);

        // When & Then
        mockMvc.perform(get("/api/holidays/audiences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("general")))
                .andExpect(jsonPath("$[0].audienceName", is("General Public")));
    }

    @Test
    void getTranslatedAudiences_ShouldReturnTranslatedAudiences() throws Exception {
        // Given
        List<Audience> audiences = Arrays.asList(audience);
        when(audienceService.getAllAudiences()).thenReturn(audiences);

        // When & Then
        mockMvc.perform(get("/api/holidays/audiences/translated")
                .param("language", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("general")))
                .andExpect(jsonPath("$[0].name", is("General Public")));
    }

    @Test
    void addAudience_ShouldCreateAndReturnAudience() throws Exception {
        // Given
        when(audienceService.addAudience(any(Audience.class))).thenReturn(audience);

        // When & Then
        mockMvc.perform(post("/api/holidays/audiences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(audience)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("general")))
                .andExpect(jsonPath("$.audienceName", is("General Public")));
    }

    @Test
    void updateAudience_ShouldUpdateAndReturnAudience() throws Exception {
        // Given
        String audienceId = "general";
        when(audienceService.updateAudience(anyString(), any(Audience.class))).thenReturn(audience);

        // When & Then
        mockMvc.perform(put("/api/holidays/audiences/{audienceId}", audienceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(audience)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("general")))
                .andExpect(jsonPath("$.audienceName", is("General Public")));
    }

    @Test
    void deleteAudience_ShouldDeleteAudience() throws Exception {
        // Given
        String audienceId = "general";

        // When & Then
        mockMvc.perform(delete("/api/holidays/audiences/{audienceId}", audienceId))
                .andExpect(status().isOk());
    }

    @Test
    void addHolidayCondition_ShouldReturnCreatedCondition() throws Exception {
        // Given
        Long holidayId = 1L;
        Object condition = new Object();
        when(holidayService.addHolidayCondition(any(Long.class), any())).thenReturn(condition);

        // When & Then
        mockMvc.perform(post("/api/holidays/{id}/conditions", holidayId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateHolidayCondition_ShouldReturnUpdatedCondition() throws Exception {
        // Given
        Long conditionId = 1L;
        Object condition = new Object();
        when(holidayService.updateHolidayCondition(any(Long.class), any())).thenReturn(condition);

        // When & Then
        mockMvc.perform(put("/api/holidays/conditions/{conditionId}", conditionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteHolidayCondition_ShouldDeleteCondition() throws Exception {
        // Given
        Long conditionId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/holidays/conditions/{conditionId}", conditionId))
                .andExpect(status().isOk());
    }

    @Test
    void addHolidaySpec_ShouldReturnCreatedSpec() throws Exception {
        // Given
        Long holidayId = 1L;
        Object spec = new Object();
        when(holidayService.addHolidaySpec(any(Long.class), any())).thenReturn(spec);

        // When & Then
        mockMvc.perform(post("/api/holidays/{id}/specs", holidayId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateHolidaySpec_ShouldReturnUpdatedSpec() throws Exception {
        // Given
        Long specId = 1L;
        Object spec = new Object();
        when(holidayService.updateHolidaySpec(any(Long.class), any())).thenReturn(spec);

        // When & Then
        mockMvc.perform(put("/api/holidays/specs/{specId}", specId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteHolidaySpec_ShouldDeleteSpec() throws Exception {
        // Given
        Long specId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/holidays/specs/{specId}", specId))
                .andExpect(status().isOk());
    }
}
