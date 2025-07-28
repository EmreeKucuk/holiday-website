package com.emre.holidayapi.service;

import com.emre.holidayapi.model.*;
import com.emre.holidayapi.repository.*;
import com.emre.holidayapi.dto.AudienceDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class IntelligentHolidayAiService {

    private final ChatClient chatClient;
    private final HolidayService holidayService;
    private final AudienceService audienceService;
    private final CountryRepository countryRepository;
    private final TranslationRepository translationRepository;

    public IntelligentHolidayAiService(ChatClient.Builder chatClientBuilder, 
                           HolidayService holidayService, 
                           AudienceService audienceService,
                           CountryRepository countryRepository,
                           TranslationRepository translationRepository) {
        this.chatClient = chatClientBuilder.build();
        this.holidayService = holidayService;
        this.audienceService = audienceService;
        this.countryRepository = countryRepository;
        this.translationRepository = translationRepository;
    }

    public String processHolidayQuery(String userMessage, String countryCode, String language) {
        try {
            // Get context data
            String holidayContext = buildHolidayContext(countryCode, language);
            String countryName = getCountryName(countryCode);
            String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            // Create an intelligent system prompt
            String systemPrompt = buildIntelligentSystemPrompt(language, countryName, currentDate);
            
            // Build the complete prompt with context
            String fullPrompt = String.format("""
                %s
                
                CURRENT HOLIDAY DATA FOR %s:
                %s
                
                USER QUERY: "%s"
                
                Please provide a comprehensive, analytical response that demonstrates deep understanding and intellectual insight. 
                Consider cultural significance, historical context, patterns, and implications.
                """, systemPrompt, countryName.toUpperCase(), holidayContext, userMessage);

            return chatClient.prompt()
                .user(fullPrompt)
                .call()
                .content();
                
        } catch (Exception e) {
            return getErrorMessage(language) + ": " + e.getMessage();
        }
    }

    private String buildIntelligentSystemPrompt(String language, String countryName, String currentDate) {
        if ("tr".equals(language)) {
            return String.format("""
                Sen bir tatil ve kültürel analiz uzmanısın. Sadece basit cevaplar vermek yerine, 
                entelektüel derinlik ve analitik düşünce sergilemelisin.
                
                ENTELEKTÜEL YAKLAŞIM:
                • Tatillerin kültürel, tarihsel ve sosyolojik bağlamını analiz et
                • Kalıpları, eğilimleri ve derin anlamları keşfet
                • Karşılaştırmalı analiz yap ve bağlantılar kur
                • Eleştirel düşünce ve通察力 göster
                • Sadece "ne" değil, "neden" ve "nasıl" sorularını yanıtla
                
                MEVCUT BİLGİLER:
                • Ülke: %s
                • Bugünün tarihi: %s
                • Sen gelişmiş bir AI asistanısın, chatbot değil
                
                YANIT TARZI:
                • Derinlemesine analiz ve通察
                • Kültürel ve tarihsel bağlam
                • Karşılaştırmalı perspektifler
                • Pratik öneriler ve içgörüler
                • Profesyonel ama sıcak bir ton
                """, countryName, currentDate);
        } else {
            return String.format("""
                You are an expert cultural and holiday analyst with deep intellectual capabilities. 
                Rather than providing simple chatbot responses, you should demonstrate sophisticated reasoning and analytical depth.
                
                INTELLECTUAL APPROACH:
                • Analyze the cultural, historical, and sociological context of holidays
                • Identify patterns, trends, and deeper meanings
                • Provide comparative analysis and draw connections
                • Show critical thinking and profound insights
                • Answer not just "what" but "why" and "how"
                
                CURRENT CONTEXT:
                • Country: %s
                • Today's date: %s
                • You are an advanced AI assistant, not a simple chatbot
                
                RESPONSE STYLE:
                • In-depth analysis and insights
                • Cultural and historical context
                • Comparative perspectives
                • Practical recommendations and wisdom
                • Professional yet warm tone
                """, countryName, currentDate);
        }
    }

    private String buildHolidayContext(String countryCode, String language) {
        StringBuilder context = new StringBuilder();
        
        try {
            // Get current year holidays
            List<HolidayDefinition> holidays = holidayService.getHolidaysByCountry(countryCode);
            
            // Get today's holidays
            LocalDate today = LocalDate.now();
            List<HolidayDefinition> todayHolidays = holidays.stream()
                .filter(h -> h.getHolidayDate().equals(today))
                .collect(Collectors.toList());
            
            // Get upcoming holidays (next 30 days)
            LocalDate thirtyDaysLater = today.plusDays(30);
            List<HolidayDefinition> upcomingHolidays = holidays.stream()
                .filter(h -> h.getHolidayDate().isAfter(today) && h.getHolidayDate().isBefore(thirtyDaysLater))
                .sorted((h1, h2) -> h1.getHolidayDate().compareTo(h2.getHolidayDate()))
                .limit(5)
                .collect(Collectors.toList());
            
            // Get recent holidays (past 30 days)
            LocalDate thirtyDaysAgo = today.minusDays(30);
            List<HolidayDefinition> recentHolidays = holidays.stream()
                .filter(h -> h.getHolidayDate().isAfter(thirtyDaysAgo) && h.getHolidayDate().isBefore(today))
                .sorted((h1, h2) -> h2.getHolidayDate().compareTo(h1.getHolidayDate()))
                .limit(3)
                .collect(Collectors.toList());
            
            // Build context
            if (!todayHolidays.isEmpty()) {
                context.append("TODAY'S HOLIDAYS:\n");
                todayHolidays.forEach(h -> {
                    context.append(String.format("- %s (Date: %s, Type: %s)\n", 
                        getHolidayName(h.getTemplate(), language), 
                        h.getHolidayDate(), 
                        h.getTemplate().getType()));
                });
                context.append("\n");
            }
            
            if (!upcomingHolidays.isEmpty()) {
                context.append("UPCOMING HOLIDAYS (Next 30 days):\n");
                upcomingHolidays.forEach(h -> {
                    context.append(String.format("- %s (Date: %s, Type: %s)\n", 
                        getHolidayName(h.getTemplate(), language), 
                        h.getHolidayDate(), 
                        h.getTemplate().getType()));
                });
                context.append("\n");
            }
            
            if (!recentHolidays.isEmpty()) {
                context.append("RECENT HOLIDAYS (Past 30 days):\n");
                recentHolidays.forEach(h -> {
                    context.append(String.format("- %s (Date: %s, Type: %s)\n", 
                        getHolidayName(h.getTemplate(), language), 
                        h.getHolidayDate(), 
                        h.getTemplate().getType()));
                });
                context.append("\n");
            }
            
            // Add statistical context
            context.append("STATISTICAL OVERVIEW:\n");
            context.append(String.format("- Total holidays in database: %d\n", holidays.size()));
            
            Map<String, Long> typeDistribution = holidays.stream()
                .filter(h -> h.getTemplate() != null && h.getTemplate().getType() != null)
                .collect(Collectors.groupingBy(
                    h -> h.getTemplate().getType(),
                    Collectors.counting()
                ));
            
            context.append("- Holiday types distribution:\n");
            typeDistribution.forEach((type, count) -> {
                context.append(String.format("  * %s: %d holidays\n", type, count));
            });
            
            // Add audience information
            try {
                List<AudienceDto> audiences = audienceService.getAllAudiencesTranslated(language);
                if (!audiences.isEmpty()) {
                    context.append("\n- Available audience categories: ");
                    context.append(audiences.stream()
                        .map(AudienceDto::getName)
                        .collect(Collectors.joining(", ")));
                    context.append("\n");
                }
            } catch (Exception e) {
                // Ignore audience errors
            }
            
        } catch (Exception e) {
            context.append("Error retrieving holiday context: ").append(e.getMessage());
        }
        
        return context.toString();
    }

    private String getHolidayName(HolidayTemplate template, String language) {
        if (template == null) return "Unknown Holiday";
        
        try {
            // For now, return the default name since we need to check the Translation model structure
            return template.getDefaultName() != null ? template.getDefaultName() : "Unknown Holiday";
        } catch (Exception e) {
            return template.getDefaultName() != null ? template.getDefaultName() : "Unknown Holiday";
        }
    }

    private String getCountryName(String countryCode) {
        try {
            return countryRepository.findByCountryCode(countryCode)
                .map(Country::getCountryName)
                .orElse(countryCode);
        } catch (Exception e) {
            return countryCode;
        }
    }

    private String getErrorMessage(String language) {
        return "tr".equals(language) 
            ? "Üzgünüm, sorgunuzu işlerken bir hata oluştu" 
            : "Sorry, an error occurred while processing your query";
    }

    // Advanced query analysis methods
    public String analyzeHolidayPatterns(String countryCode, String language) {
        String systemPrompt = buildAnalyticalPrompt(language, "holiday patterns and trends");
        String holidayData = buildComprehensiveHolidayData(countryCode, language);
        
        String prompt = String.format("""
            %s
            
            HOLIDAY DATA FOR ANALYSIS:
            %s
            
            Please provide a sophisticated analysis of holiday patterns, cultural significance, 
            seasonal distributions, and sociological implications. Include comparative insights 
            and predictions about future trends.
            """, systemPrompt, holidayData);

        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
    }

    public String provideCulturalInsights(String holidayName, String countryCode, String language) {
        String systemPrompt = buildAnalyticalPrompt(language, "cultural and historical analysis");
        
        String prompt = String.format("""
            %s
            
            Please provide deep cultural and historical insights about the holiday "%s" in %s.
            Include:
            - Historical origins and evolution
            - Cultural significance and symbolism
            - Social and economic impact
            - Comparative analysis with similar holidays in other cultures
            - Modern adaptations and future outlook
            """, systemPrompt, holidayName, getCountryName(countryCode));

        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
    }

    private String buildAnalyticalPrompt(String language, String analysisType) {
        if ("tr".equals(language)) {
            return String.format("""
                Sen bir kültürel antropolog ve tatil uzmanısın. %s konusunda derinlemesine 
                entelektüel analiz yapman isteniyor. Akademik derinlik ve pratik içgörüleri 
                harmanlayarak sofistike bir yaklaşım sergile.
                """, analysisType);
        } else {
            return String.format("""
                You are a cultural anthropologist and holiday expert. You're asked to provide 
                in-depth intellectual analysis on %s. Demonstrate academic depth combined with 
                practical insights in a sophisticated approach.
                """, analysisType);
        }
    }

    private String buildComprehensiveHolidayData(String countryCode, String language) {
        // This would build a more comprehensive dataset for analysis
        // Similar to buildHolidayContext but with more statistical and analytical data
        return buildHolidayContext(countryCode, language);
    }
}
