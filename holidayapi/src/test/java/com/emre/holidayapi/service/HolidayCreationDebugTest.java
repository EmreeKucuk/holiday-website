package com.emre.holidayapi.service;

import com.emre.holidayapi.model.*;
import com.emre.holidayapi.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HolidayCreationDebugTest {

    @Test
    public void testHolidayCreationLogic() {
        // Mock all dependencies
        ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        HolidayService holidayService = mock(HolidayService.class);
        HolidayTemplateService holidayTemplateService = mock(HolidayTemplateService.class);
        AudienceService audienceService = mock(AudienceService.class);
        CountryRepository countryRepository = mock(CountryRepository.class);
        TranslationRepository translationRepository = mock(TranslationRepository.class);
        HolidayAudienceRepository holidayAudienceRepository = mock(HolidayAudienceRepository.class);

        when(chatClientBuilder.build()).thenReturn(chatClient);

        // Create test data
        HolidayTemplate template = new HolidayTemplate();
        template.setId(1L);
        template.setCode("ordinary_day_at_internship");
        template.setDefaultName("Ordinary Day at Internship");
        template.setType("CUSTOM");

        HolidayDefinition holiday = new HolidayDefinition();
        holiday.setId(1L);
        holiday.setTemplate(template);
        holiday.setHolidayDate(LocalDate.now());

        Audience audience = new Audience();
        audience.setCode("general");
        audience.setAudienceName("General Public");

        // Setup mocks
        when(holidayTemplateService.findByCode("ordinary_day_at_internship")).thenReturn(null);
        when(holidayTemplateService.createTemplate(any(HolidayTemplate.class))).thenReturn(template);
        when(holidayService.createHoliday(any(HolidayDefinition.class))).thenReturn(holiday);
        when(audienceService.getAllAudiences()).thenReturn(Arrays.asList(audience));
        when(holidayAudienceRepository.save(any(HolidayAudience.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Create service
        HolidayAiService service = new HolidayAiService(
            chatClientBuilder, holidayService, holidayTemplateService, 
            audienceService, countryRepository, translationRepository, holidayAudienceRepository
        );

        // Test holiday creation
        try {
            String result = service.processHolidayQuery(
                "add a holiday called \"Ordinary Day at Internship\" to today for only \"general public\" audience",
                "TR", "en"
            );
            
            System.out.println("Result: " + result);
            
            // Verify the methods were called
            verify(holidayTemplateService).createTemplate(any(HolidayTemplate.class));
            verify(holidayService).createHoliday(any(HolidayDefinition.class));
            verify(holidayAudienceRepository).save(any(HolidayAudience.class));
            
        } catch (Exception e) {
            System.err.println("Exception during holiday creation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
