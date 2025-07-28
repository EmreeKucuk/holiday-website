package com.emre.holidayapi.service;

import com.emre.holidayapi.model.HolidayTemplate;
import com.emre.holidayapi.repository.HolidayTemplateRepository;
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
class HolidayTemplateServiceTest {

    @Mock
    private HolidayTemplateRepository repository;

    @InjectMocks
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
    void getAllTemplates_ShouldReturnAllTemplates() {
        // Given
        List<HolidayTemplate> expectedTemplates = Arrays.asList(template1, template2);
        when(repository.findAll()).thenReturn(expectedTemplates);

        // When
        List<HolidayTemplate> actualTemplates = holidayTemplateService.getAllTemplates();

        // Then
        assertThat(actualTemplates)
                .hasSize(2)
                .containsExactly(template1, template2);
        verify(repository).findAll();
    }

    @Test
    void getAllTemplates_WhenNoTemplates_ShouldReturnEmptyList() {
        // Given
        when(repository.findAll()).thenReturn(Arrays.asList());

        // When
        List<HolidayTemplate> actualTemplates = holidayTemplateService.getAllTemplates();

        // Then
        assertThat(actualTemplates).isEmpty();
        verify(repository).findAll();
    }

    @Test
    void getAllTemplates_ShouldCallRepositoryOnce() {
        // Given
        List<HolidayTemplate> expectedTemplates = Arrays.asList(template1);
        when(repository.findAll()).thenReturn(expectedTemplates);

        // When
        holidayTemplateService.getAllTemplates();

        // Then
        verify(repository, times(1)).findAll();
    }

    @Test
    void getAllTemplates_ShouldReturnCorrectTemplateProperties() {
        // Given
        List<HolidayTemplate> expectedTemplates = Arrays.asList(template1);
        when(repository.findAll()).thenReturn(expectedTemplates);

        // When
        List<HolidayTemplate> actualTemplates = holidayTemplateService.getAllTemplates();

        // Then
        assertThat(actualTemplates).hasSize(1);
        HolidayTemplate actualTemplate = actualTemplates.get(0);
        assertThat(actualTemplate.getId()).isEqualTo(1L);
        assertThat(actualTemplate.getCode()).isEqualTo("new_year");
        assertThat(actualTemplate.getDefaultName()).isEqualTo("New Year");
        assertThat(actualTemplate.getType()).isEqualTo("Official");
        verify(repository).findAll();
    }
}
