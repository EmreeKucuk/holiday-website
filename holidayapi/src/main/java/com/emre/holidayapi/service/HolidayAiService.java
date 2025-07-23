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
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class HolidayAiService {

    private final ChatClient chatClient;
    private final HolidayService holidayService;
    private final AudienceService audienceService;
    private final CountryRepository countryRepository;
    private final TranslationRepository translationRepository;

    public HolidayAiService(ChatClient.Builder chatClientBuilder, 
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
            String lowerMessage = userMessage.toLowerCase();
            
            // Check if asking about today's holiday
            if (lowerMessage.contains("today") && lowerMessage.contains("holiday")) {
                return handleTodayHolidayQuery(countryCode, language);
            }
            
            // Check if asking about holidays between dates
            if (containsDateRange(lowerMessage)) {
                return handleDateRangeQuery(userMessage, countryCode, language);
            }
            
            // Check if asking about annual holidays
            if (lowerMessage.contains("year") && (lowerMessage.contains("holiday") || lowerMessage.contains("how many"))) {
                return handleAnnualHolidayQuery(countryCode, language);
            }
            
            // Check if asking about audience-specific holidays
            if (containsAudienceKeywords(lowerMessage)) {
                return handleAudienceSpecificQuery(userMessage, countryCode, language);
            }
            
            // For general queries, use AI with context
            return handleGeneralQuery(userMessage, countryCode, language);
            
        } catch (Exception e) {
            return "I'm sorry, I encountered an error while processing your request. Please try again or rephrase your question.";
        }
    }

    private String handleTodayHolidayQuery(String countryCode, String language) {
        LocalDate today = LocalDate.now();
        List<HolidayDefinition> todayHolidays = holidayService.getHolidaysByDate(today, countryCode);
        
        if (todayHolidays.isEmpty()) {
            return String.format("No, there are no holidays today (%s) in %s.", 
                today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                getCountryName(countryCode));
        } else {
            String holidayNames = todayHolidays.stream()
                .map(h -> getHolidayName(h.getTemplate(), language))
                .collect(Collectors.joining(", "));
            return String.format("Yes! Today (%s) is a holiday in %s: %s", 
                today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                getCountryName(countryCode), 
                holidayNames);
        }
    }

    private String handleDateRangeQuery(String userMessage, String countryCode, String language) {
        // Extract dates from message
        List<LocalDate> dates = extractDatesFromMessage(userMessage);
        if (dates.size() < 2) {
            return "I couldn't find valid date range in your message. Please use format like '01/01/2025 - 31/01/2025' or 'between 01/01/2025 and 31/01/2025'.";
        }
        
        LocalDate startDate = dates.get(0);
        LocalDate endDate = dates.get(1);
        
        // Check if asking about working days calculation
        if (userMessage.toLowerCase().contains("working") || userMessage.toLowerCase().contains("calculate")) {
            return handleWorkingDaysCalculation(startDate, endDate, countryCode, userMessage.toLowerCase().contains("weekend"));
        }
        
        // Get holidays in date range
        List<HolidayDefinition> holidays = holidayService.getHolidaysByDateRange(startDate, endDate, countryCode);
        
        if (holidays.isEmpty()) {
            return String.format("There are no holidays between %s and %s in %s.", 
                startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                getCountryName(countryCode));
        } else {
            StringBuilder response = new StringBuilder();
            response.append(String.format("Here are the holidays between %s and %s in %s:\n\n", 
                startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                getCountryName(countryCode)));
            
            for (HolidayDefinition holiday : holidays) {
                response.append(String.format("• %s - %s\n", 
                    getHolidayName(holiday.getTemplate(), language),
                    holiday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            }
            
            return response.toString();
        }
    }

    private String handleWorkingDaysCalculation(LocalDate startDate, LocalDate endDate, String countryCode, boolean includeWeekends) {
        List<HolidayDefinition> holidays = holidayService.getHolidaysByDateRange(startDate, endDate, countryCode);
        
        int totalDays = (int) startDate.until(endDate).getDays() + 1;
        int holidayDays = holidays.size();
        
        int weekendDays = 0;
        if (!includeWeekends) {
            LocalDate current = startDate;
            while (!current.isAfter(endDate)) {
                if (current.getDayOfWeek().getValue() >= 6) { // Saturday = 6, Sunday = 7
                    weekendDays++;
                }
                current = current.plusDays(1);
            }
        }
        
        int workingDays = totalDays - holidayDays - (includeWeekends ? 0 : weekendDays);
        
        return String.format("Between %s and %s in %s:\n" +
                "• Total days: %d\n" +
                "• Holiday days: %d\n" +
                "%s" +
                "• Working days: %d", 
                startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                getCountryName(countryCode),
                totalDays,
                holidayDays,
                includeWeekends ? "" : String.format("• Weekend days: %d\n", weekendDays),
                workingDays);
    }

    private String handleAnnualHolidayQuery(String countryCode, String language) {
        int currentYear = LocalDate.now().getYear();
        LocalDate startOfYear = LocalDate.of(currentYear, 1, 1);
        LocalDate endOfYear = LocalDate.of(currentYear, 12, 31);
        
        List<HolidayDefinition> yearlyHolidays = holidayService.getHolidaysByDateRange(startOfYear, endOfYear, countryCode);
        
        return String.format("In %d, %s has %d holidays throughout the year.", 
            currentYear, getCountryName(countryCode), yearlyHolidays.size());
    }

    private String handleAudienceSpecificQuery(String userMessage, String countryCode, String language) {
        // Extract audience from message
        String audience = extractAudienceFromMessage(userMessage, language);
        if (audience == null) {
            return "I couldn't identify the specific audience you're asking about. Please mention a specific group like 'students', 'government employees', 'private sector', etc.";
        }
        
        int currentYear = LocalDate.now().getYear();
        LocalDate startOfYear = LocalDate.of(currentYear, 1, 1);
        LocalDate endOfYear = LocalDate.of(currentYear, 12, 31);
        
        List<HolidayDefinition> audienceHolidays = holidayService.getHolidaysByDateRangeAndAudience(
            startOfYear, endOfYear, countryCode, audience);
        
        if (audienceHolidays.isEmpty()) {
            return String.format("I couldn't find any holidays specifically for %s in %s this year.", 
                audience, getCountryName(countryCode));
        }
        
        StringBuilder response = new StringBuilder();
        response.append(String.format("Holidays for %s in %s (%d):\n\n", 
            audience, getCountryName(countryCode), currentYear));
        
        for (HolidayDefinition holiday : audienceHolidays) {
            response.append(String.format("• %s - %s\n", 
                getHolidayName(holiday.getTemplate(), language),
                holiday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        }
        
        return response.toString();
    }

    private String handleGeneralQuery(String userMessage, String countryCode, String language) {
        // Create context about the holiday system
        String context = createHolidayContext(countryCode, language);
        
        String systemPrompt = String.format("""
            You are a helpful holiday assistant for %s. You help users with questions about holidays, working days, and calendar information.
            
            Current context:
            %s
            
            Instructions:
            - Be helpful and friendly
            - Provide specific information when possible
            - If you can't answer something specific about holidays, suggest they ask about:
              * Today's holidays
              * Holidays in a date range
              * Annual holiday count
              * Audience-specific holidays
            - Keep responses concise but informative
            - Use the date format dd/MM/yyyy when mentioning dates
            """, getCountryName(countryCode), context);
        
        PromptTemplate template = new PromptTemplate(systemPrompt + "\n\nUser question: {question}");
        Prompt prompt = template.create(Map.of("question", userMessage));
        
        return chatClient.prompt(prompt).call().content();
    }

    private String createHolidayContext(String countryCode, String language) {
        int currentYear = LocalDate.now().getYear();
        LocalDate startOfYear = LocalDate.of(currentYear, 1, 1);
        LocalDate endOfYear = LocalDate.of(currentYear, 12, 31);
        
        List<HolidayDefinition> yearHolidays = holidayService.getHolidaysByDateRange(startOfYear, endOfYear, countryCode);
        List<AudienceDto> audiences = audienceService.getAllAudiencesTranslated(language);
        
        StringBuilder context = new StringBuilder();
        context.append(String.format("Country: %s\n", getCountryName(countryCode)));
        context.append(String.format("Total holidays this year (%d): %d\n", currentYear, yearHolidays.size()));
        context.append("Available audience types: ");
        context.append(audiences.stream()
            .map(AudienceDto::getName)
            .collect(Collectors.joining(", ")));
        
        return context.toString();
    }

    // Helper methods
    private boolean containsDateRange(String message) {
        // Look for date patterns like "01/01/2025 - 31/01/2025" or "between ... and ..."
        Pattern dateRangePattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{4}.*\\d{1,2}/\\d{1,2}/\\d{4}");
        return dateRangePattern.matcher(message).find() || 
               (message.contains("between") && message.contains("and"));
    }

    private boolean containsAudienceKeywords(String message) {
        return message.contains("student") || message.contains("employee") || 
               message.contains("government") || message.contains("private") ||
               message.contains("audience") || message.contains("group");
    }

    private List<LocalDate> extractDatesFromMessage(String message) {
        List<LocalDate> dates = new ArrayList<>();
        Pattern datePattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{4}");
        Matcher matcher = datePattern.matcher(message);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        while (matcher.find()) {
            try {
                LocalDate date = LocalDate.parse(matcher.group(), formatter);
                dates.add(date);
            } catch (DateTimeParseException e) {
                // Ignore invalid dates
            }
        }
        
        return dates;
    }

    private String extractAudienceFromMessage(String message, String language) {
        List<AudienceDto> audiences = audienceService.getAllAudiencesTranslated(language);
        
        for (AudienceDto audience : audiences) {
            if (message.toLowerCase().contains(audience.getName().toLowerCase()) ||
                message.toLowerCase().contains(audience.getCode().toLowerCase())) {
                return audience.getCode();
            }
        }
        
        // Fallback to common keywords
        if (message.toLowerCase().contains("student")) return "STUDENTS";
        if (message.toLowerCase().contains("government")) return "GOVERNMENT";
        if (message.toLowerCase().contains("private")) return "PRIVATE_SECTOR";
        
        return null;
    }

    private String getCountryName(String countryCode) {
        return countryRepository.findByCountryCode(countryCode)
            .map(Country::getCountryName)
            .orElse(countryCode);
    }

    private String getHolidayName(HolidayTemplate template, String language) {
        return translationRepository.findByTemplateIdAndLanguageCode(template.getId(), language)
            .map(Translation::getTranslatedName)
            .orElse(template.getDefaultName());
    }
}
