package com.emre.holidayapi.controller;

import com.emre.holidayapi.model.HolidayTemplate;
import com.emre.holidayapi.service.HolidayTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HolidayTemplateController.class)
class HolidayTemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HolidayTemplateService holidayTemplateService;

    private HolidayTemplate template1;
    private HolidayTemplate template2;

    @BeforeEach
    void setUp() {
        template1 = new HolidayTemplate();
        template1.setId(1L);
        template1.setCode("new_year");
        template1.setDefaultName("New Year");
        template1.setType("Official");

        template2 = new HolidayTemplate();
        template2.setId(2L);
        template2.setCode("christmas");
        template2.setDefaultName("Christmas");
        template2.setType("Religious");
    }

    @Test
    void getAllTemplates_ShouldReturnAllTemplates() throws Exception {
        // Given
        List<HolidayTemplate> templates = Arrays.asList(template1, template2);
        when(holidayTemplateService.getAllTemplates()).thenReturn(templates);

        // When & Then
        mockMvc.perform(get("/api/holiday-templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].code", is("new_year")))
                .andExpect(jsonPath("$[0].defaultName", is("New Year")))
                .andExpect(jsonPath("$[0].type", is("Official")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].code", is("christmas")))
                .andExpect(jsonPath("$[1].defaultName", is("Christmas")))
                .andExpect(jsonPath("$[1].type", is("Religious")));
    }

    @Test
    void getAllTemplates_WhenNoTemplates_ShouldReturnEmptyList() throws Exception {
        // Given
        when(holidayTemplateService.getAllTemplates()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/holiday-templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllTemplates_ShouldReturnCorrectContentType() throws Exception {
        // Given
        List<HolidayTemplate> templates = Arrays.asList(template1);
        when(holidayTemplateService.getAllTemplates()).thenReturn(templates);

        // When & Then
        mockMvc.perform(get("/api/holiday-templates"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void getAllTemplates_ShouldReturnTemplateWithAllProperties() throws Exception {
        // Given
        List<HolidayTemplate> templates = Arrays.asList(template1);
        when(holidayTemplateService.getAllTemplates()).thenReturn(templates);

        // When & Then
        mockMvc.perform(get("/api/holiday-templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].defaultName").exists())
                .andExpect(jsonPath("$[0].type").exists());
    }
}
