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
            
            // Check for specific holiday name recognition
            if (containsHolidayNameQuery(lowerMessage)) {
                return handleHolidayNameQuery(userMessage, countryCode, language);
            }
            
            // Check for specific year queries
            if (containsSpecificYear(lowerMessage)) {
                return handleSpecificYearQuery(userMessage, countryCode, language);
            }
            
            // Check for holiday statistics queries
            if (containsStatisticsKeywords(lowerMessage)) {
                return handleStatisticsQuery(userMessage, countryCode, language);
            }
            
            // Check for holiday type queries (religious, official, etc.)
            if (containsHolidayTypeKeywords(lowerMessage)) {
                return handleHolidayTypeQuery(userMessage, countryCode, language);
            }
            
            // Check for vacation optimization queries
            if (containsVacationOptimizationKeywords(lowerMessage)) {
                return handleVacationOptimizationQuery(userMessage, countryCode, language);
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
            return getLocalizedErrorMessage(language);
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
            You are an intelligent and friendly holiday assistant designed to help users with country-specific holiday-related questions for %s. 
            You are connected to a backend system that provides detailed information about public holidays, working days, and audience-specific holidays.

            Your job is to interpret the user's natural language input, and — based on provided context or API output — respond with clear, informative answers in %s.

            You support the following functionalities:

            1. ✅ **Today's Holidays**: If the user asks "Is there a holiday today?" or similar, respond with whether today is a holiday in the selected country and what holidays are being observed.

            2. ✅ **Date Range Queries**: If the user asks about holidays between two dates (e.g., "What holidays are there from 01/01/2025 to 15/01/2025?"), list all holidays within that range.

            3. ✅ **Working Day Calculations**: If the user asks "How many working days between 01/01/2025 and 31/01/2025?", return the number of working days, excluding holidays and optionally weekends.

            4. ✅ **Annual Holiday Count**: If the user asks "How many holidays are there this year in %s?", return the total number of holidays in the current year for that country.

            5. ✅ **Audience-Specific Holidays**: If the user includes audience types like "students", "government employees", "private sector" etc., return holidays that are only valid for that group.

            6. ✅ **General Questions**: If a question doesn't match the patterns above, try to answer it by using the provided country context and year statistics.

            7. 🔄 **Holiday Name Recognition**: If the user asks a question like "When is Ramazan Bayramı?" or "Tell me about 23 Nisan", extract the holiday name and return its date and description if available.

            8. 📅 **Holidays in a Specific Year**: If the user specifies a year like "2024" or asks "What are the holidays in 2024 in %s?", return a list of holidays for that year.

            9. 📊 **Holiday Statistics**: Support questions like:
               - "Which month has the most holidays?"
               - "What's the longest holiday this year?"
               - "How many holidays fall on weekends?"

            10. 🕌 **Holiday Type Classification**: If the user asks for "religious holidays" or "official public holidays", filter results based on the type of holiday.

            11. 📆 **Holiday Duration Analysis**: If a holiday spans multiple days or extends into a weekend, highlight its total duration (e.g., "Ramazan Bayramı lasts for 3 days").

            12. 🌍 **Language-Aware Responses**: Always respond in %s. Adjust your tone and phrasing accordingly.

            Current context:
            %s
            
            Instructions:
            - Be concise, but informative and polite
            - Use date format dd/MM/yyyy in your responses  
            - Always tailor your answer to the country and year provided
            - If you cannot find an exact answer, suggest users ask about:
              * Holidays today
              * Holidays between specific dates
              * Yearly holiday totals
              * Holidays for specific audiences (students, employees, etc.)
            - Act as a true holiday expert for %s
            """, getCountryName(countryCode), language, getCountryName(countryCode), getCountryName(countryCode), language, context, getCountryName(countryCode));
        
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

    // New methods for extended functionality

    private boolean containsHolidayNameQuery(String message) {
        return message.contains("when is") || message.contains("tell me about") || 
               message.contains("what is") || message.contains("about") ||
               message.contains("how long") || message.contains("duration") ||
               message.contains("last");
    }

    private boolean containsSpecificYear(String message) {
        Pattern yearPattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        return yearPattern.matcher(message).find() || message.contains("this year") || message.contains("next year");
    }

    private boolean containsStatisticsKeywords(String message) {
        return message.contains("most holidays") || message.contains("longest holiday") ||
               message.contains("weekends") || message.contains("which month") ||
               message.contains("statistics") || message.contains("how many fall");
    }

    private boolean containsHolidayTypeKeywords(String message) {
        return message.contains("religious") || message.contains("official") ||
               message.contains("public") || message.contains("cultural") ||
               message.contains("national") || message.contains("type") ||
               message.contains("show me") && (message.contains("religious") || 
               message.contains("official") || message.contains("cultural"));
    }

    private boolean containsVacationOptimizationKeywords(String message) {
        return (message.contains("vacation") || message.contains("holiday") || message.contains("leave")) &&
               (message.contains("longest") || message.contains("optimize") || message.contains("maximize") ||
                message.contains("best time") || message.contains("optimal") || message.contains("connect") ||
                message.contains("bridge") || message.contains("extend") || message.contains("tatil") ||
                message.contains("izin") || message.contains("en uzun") || message.contains("bağla") ||
                message.contains("weekend") || message.contains("hafta sonu"));
    }

    private String handleHolidayNameQuery(String userMessage, String countryCode, String language) {
        // Extract potential holiday name from the message
        String potentialHolidayName = extractHolidayNameFromMessage(userMessage, language);
        
        if (potentialHolidayName == null) {
            return getLocalizedMessage(language, 
                "I couldn't identify a specific holiday name in your question. Please mention a holiday name like 'Christmas', 'Ramazan Bayramı', etc.",
                "Sorunuzda belirli bir tatil adı bulamadım. Lütfen 'Noel', 'Ramazan Bayramı' gibi bir tatil adı belirtin.");
        }

        // Search for holidays by name in current and next year
        int currentYear = LocalDate.now().getYear();
        LocalDate startDate = LocalDate.of(currentYear, 1, 1);
        LocalDate endDate = LocalDate.of(currentYear + 1, 12, 31);
        
        List<HolidayDefinition> holidays = holidayService.getHolidaysByDateRange(startDate, endDate, countryCode);
        
        // Find matching holidays by name
        List<HolidayDefinition> matchingHolidays = holidays.stream()
            .filter(h -> getHolidayName(h.getTemplate(), language).toLowerCase()
                        .contains(potentialHolidayName.toLowerCase()))
            .collect(Collectors.toList());

        if (matchingHolidays.isEmpty()) {
            return getLocalizedMessage(language,
                String.format("I couldn't find information about '%s' in %s for %d-%d.", 
                    potentialHolidayName, getCountryName(countryCode), currentYear, currentYear + 1),
                String.format("'%s' hakkında %s'de %d-%d yılları için bilgi bulamadım.", 
                    potentialHolidayName, getCountryName(countryCode), currentYear, currentYear + 1));
        }

        // Check if asking about duration specifically
        boolean askingAboutDuration = userMessage.toLowerCase().contains("how long") || 
                                    userMessage.toLowerCase().contains("duration") || 
                                    userMessage.toLowerCase().contains("last") ||
                                    userMessage.toLowerCase().contains("kaç gün") ||
                                    userMessage.toLowerCase().contains("ne kadar");

        if (askingAboutDuration) {
            // Use the detailed duration analysis for duration questions
            return analyzeDuration(potentialHolidayName, language);
        }

        StringBuilder response = new StringBuilder();
        for (HolidayDefinition holiday : matchingHolidays) {
            String holidayName = getHolidayName(holiday.getTemplate(), language);
            String dateStr = holiday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            response.append(String.format("%s is on %s", holidayName, dateStr));
            
            // Add duration analysis
            String duration = analyzeDuration(holiday, countryCode);
            if (!duration.isEmpty()) {
                response.append(duration);
            }
            response.append("\n");
        }

        return response.toString().trim();
    }

    private String handleSpecificYearQuery(String userMessage, String countryCode, String language) {
        int requestedYear = extractYearFromMessage(userMessage);
        if (requestedYear == -1) {
            requestedYear = LocalDate.now().getYear();
        }

        LocalDate startOfYear = LocalDate.of(requestedYear, 1, 1);
        LocalDate endOfYear = LocalDate.of(requestedYear, 12, 31);
        
        List<HolidayDefinition> yearHolidays = holidayService.getHolidaysByDateRange(startOfYear, endOfYear, countryCode);
        
        if (yearHolidays.isEmpty()) {
            return getLocalizedMessage(language,
                String.format("No holidays found for %d in %s.", requestedYear, getCountryName(countryCode)),
                String.format("%d yılında %s için tatil bulunamadı.", requestedYear, getCountryName(countryCode)));
        }

        StringBuilder response = new StringBuilder();
        response.append(getLocalizedMessage(language,
            String.format("Holidays in %s for %d:\n\n", getCountryName(countryCode), requestedYear),
            String.format("%d yılında %s tatilleri:\n\n", requestedYear, getCountryName(countryCode))));
        
        for (HolidayDefinition holiday : yearHolidays) {
            response.append(String.format("• %s - %s\n", 
                getHolidayName(holiday.getTemplate(), language),
                holiday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        }
        
        return response.toString();
    }

    private String handleStatisticsQuery(String userMessage, String countryCode, String language) {
        int currentYear = LocalDate.now().getYear();
        LocalDate startOfYear = LocalDate.of(currentYear, 1, 1);
        LocalDate endOfYear = LocalDate.of(currentYear, 12, 31);
        
        List<HolidayDefinition> yearHolidays = holidayService.getHolidaysByDateRange(startOfYear, endOfYear, countryCode);
        
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("which month") && lowerMessage.contains("most")) {
            return analyzeMonthlyDistribution(yearHolidays, language, countryCode);
        }
        
        if (lowerMessage.contains("longest")) {
            return analyzeLongestHoliday(yearHolidays, language, countryCode);
        }
        
        if (lowerMessage.contains("weekend")) {
            return analyzeWeekendHolidays(yearHolidays, language, countryCode);
        }
        
        // General statistics
        return generateGeneralStatistics(yearHolidays, language, countryCode, currentYear);
    }

    private String handleHolidayTypeQuery(String userMessage, String countryCode, String language) {
        String lowerMessage = userMessage.toLowerCase();
        String requestedType = null;
        
        if (lowerMessage.contains("religious")) requestedType = "RELIGIOUS";
        else if (lowerMessage.contains("official") || lowerMessage.contains("public")) requestedType = "OFFICIAL";
        else if (lowerMessage.contains("cultural")) requestedType = "CULTURAL";
        else if (lowerMessage.contains("national")) requestedType = "NATIONAL";
        
        // Check if this is a combined date range + type query
        List<LocalDate> dates = extractDatesFromMessage(userMessage);
        LocalDate startDate, endDate;
        
        if (dates.size() >= 2) {
            // Date range specified
            startDate = dates.get(0);
            endDate = dates.get(1);
        } else if (containsMonthNames(lowerMessage)) {
            // Month names specified (like "between June and August")
            int currentYear = LocalDate.now().getYear();
            int[] monthRange = extractMonthRange(lowerMessage);
            if (monthRange != null) {
                startDate = LocalDate.of(currentYear, monthRange[0], 1);
                endDate = LocalDate.of(currentYear, monthRange[1], 
                    LocalDate.of(currentYear, monthRange[1], 1).lengthOfMonth());
            } else {
                // Default to current year
                startDate = LocalDate.of(currentYear, 1, 1);
                endDate = LocalDate.of(currentYear, 12, 31);
            }
        } else {
            // Default to current year
            int currentYear = LocalDate.now().getYear();
            startDate = LocalDate.of(currentYear, 1, 1);
            endDate = LocalDate.of(currentYear, 12, 31);
        }
        
        List<HolidayDefinition> holidays = holidayService.getHolidaysByDateRange(startDate, endDate, countryCode);
        
        // Filter by type using holiday template type or name-based filtering
        List<HolidayDefinition> filteredHolidays = filterHolidaysByType(holidays, requestedType, language);
        
        StringBuilder response = new StringBuilder();
        if (requestedType != null) {
            if (dates.size() >= 2 || containsMonthNames(lowerMessage)) {
                response.append(getLocalizedMessage(language,
                    String.format("%s holidays between %s and %s in %s:\n\n", 
                        requestedType.toLowerCase(), 
                        startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        getCountryName(countryCode)),
                    String.format("%s ile %s arasında %s'deki %s tatilleri:\n\n", 
                        startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        getCountryName(countryCode), requestedType.toLowerCase())));
            } else {
                response.append(getLocalizedMessage(language,
                    String.format("%s holidays in %s for %d:\n\n", 
                        requestedType.toLowerCase(), getCountryName(countryCode), startDate.getYear()),
                    String.format("%d yılında %s'deki %s tatilleri:\n\n", 
                        startDate.getYear(), getCountryName(countryCode), requestedType.toLowerCase())));
            }
        } else {
            response.append(getLocalizedMessage(language,
                String.format("All holidays in %s for %d:\n\n", getCountryName(countryCode), startDate.getYear()),
                String.format("%d yılında %s'deki tüm tatiller:\n\n", startDate.getYear(), getCountryName(countryCode))));
        }
        
        if (filteredHolidays.isEmpty()) {
            response.append(getLocalizedMessage(language,
                "No holidays found matching your criteria.",
                "Kriterlerinize uyan tatil bulunamadı."));
        } else {
            for (HolidayDefinition holiday : filteredHolidays) {
                response.append(String.format("• %s - %s\n", 
                    getHolidayName(holiday.getTemplate(), language),
                    holiday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            }
        }
        
        return response.toString();
    }

    private String handleVacationOptimizationQuery(String userMessage, String countryCode, String language) {
        try {
            // Extract year and period preferences from the message
            int requestedYear = extractYearFromMessage(userMessage);
            if (requestedYear == -1) {
                requestedYear = LocalDate.now().getYear();
            }
            
            // Check if user has specified vacation days in their message
            int maxVacationDays = extractMaxVacationDays(userMessage);
            
            // If no vacation days specified, ask the user first
            if (maxVacationDays == -1) {
                return getLocalizedMessage(language,
                    String.format("To help you find the best vacation opportunities in %s for %d, please let me know:\n\n" +
                        "How many vacation days can you take? (For example: \"I can take 5 vacation days\" or \"maximum 10 days\")\n\n" +
                        "Once you tell me your limit, I'll calculate the best periods to maximize your time off by combining your vacation days with holidays and weekends.",
                        getCountryName(countryCode), requestedYear),
                    String.format("%d yılında %s'deki en iyi tatil fırsatlarını bulmanıza yardım etmek için lütfen bana şunu söyleyin:\n\n" +
                        "Kaç izin günü kullanabilirsiniz? (Örneğin: \"5 izin günü kullanabilirim\" veya \"maksimum 10 gün\")\n\n" +
                        "Limitinizi söyledikten sonra, izin günlerinizi tatiller ve hafta sonları ile birleştirerek en uzun tatil süresini elde etmenin en iyi yollarını hesaplayacağım.",
                        requestedYear, getCountryName(countryCode)));
            }
            
            LocalDate startOfYear = LocalDate.of(requestedYear, 1, 1);
            LocalDate endOfYear = LocalDate.of(requestedYear, 12, 31);
            
            List<HolidayDefinition> yearHolidays = holidayService.getHolidaysByDateRange(startOfYear, endOfYear, countryCode);
            
            // Find optimal vacation periods
            List<VacationOptimization> optimizations = findOptimalVacationPeriods(yearHolidays, requestedYear, maxVacationDays);
            
            StringBuilder response = new StringBuilder();
            response.append(getLocalizedMessage(language,
                String.format("Based on your %d available vacation days, here are the best vacation optimization opportunities in %s for %d:\n\n", 
                    maxVacationDays, getCountryName(countryCode), requestedYear),
                String.format("%d mevcut izin gününüze göre, %d yılında %s'de en iyi tatil optimizasyonu fırsatları:\n\n", 
                    maxVacationDays, requestedYear, getCountryName(countryCode))));
            
            if (optimizations.isEmpty()) {
                response.append(getLocalizedMessage(language,
                    String.format("No significant vacation optimization opportunities found for %d vacation days this year. " +
                        "Try increasing your vacation day limit or consider different periods.", maxVacationDays),
                    String.format("Bu yıl %d izin günü için önemli tatil optimizasyonu fırsatı bulunamadı. " +
                        "İzin günü limitinizi artırmayı veya farklı dönemleri değerlendirmeyi deneyin.", maxVacationDays)));
            } else {
                for (int i = 0; i < Math.min(3, optimizations.size()); i++) {
                    VacationOptimization opt = optimizations.get(i);
                    response.append(getLocalizedMessage(language,
                        String.format("🏖️ Option %d: Take %d vacation days from %s to %s\n" +
                            "   • Total time off: %d days (%s to %s)\n" +
                            "   • Efficiency: %.1f days off per vacation day\n" +
                            "   • Includes: %s\n\n",
                            i + 1,
                            opt.vacationDaysNeeded,
                            opt.vacationStartDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            opt.vacationEndDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            opt.totalDaysOff,
                            opt.totalStartDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            opt.totalEndDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            opt.efficiency,
                            opt.description),
                        String.format("🏖️ Seçenek %d: %s - %s arası %d izin günü al\n" +
                            "   • Toplam tatil süresi: %d gün (%s - %s)\n" +
                            "   • Verimlilik: izin günü başına %.1f tatil günü\n" +
                            "   • İçerir: %s\n\n",
                            i + 1,
                            opt.vacationStartDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            opt.vacationEndDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            opt.vacationDaysNeeded,
                            opt.totalDaysOff,
                            opt.totalStartDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            opt.totalEndDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            opt.efficiency,
                            opt.description)));
                }
            }
            
            return response.toString();
            
        } catch (Exception e) {
            // Log the error for debugging (in a real app you'd use a logger)
            System.err.println("Error in vacation optimization: " + e.getMessage());
            e.printStackTrace();
            
            return getLocalizedMessage(language,
                "I encountered an error while calculating vacation optimization. Please try rephrasing your question with a specific number of vacation days, like 'Find best vacation with 5 days'.",
                "Tatil optimizasyonu hesaplarken bir hata oluştu. Lütfen '5 günle en iyi tatil bul' gibi belirli bir izin günü sayısı ile sorunuzu yeniden ifade edin.");
        }
    }

    // Helper class for vacation optimization
    private static class VacationOptimization {
        LocalDate vacationStartDate;
        LocalDate vacationEndDate;
        LocalDate totalStartDate;
        LocalDate totalEndDate;
        int vacationDaysNeeded;
        int totalDaysOff;
        double efficiency;
        String description;
        
        VacationOptimization(LocalDate vacationStart, LocalDate vacationEnd, 
                           LocalDate totalStart, LocalDate totalEnd,
                           int vacationDays, int totalDays, String desc) {
            this.vacationStartDate = vacationStart;
            this.vacationEndDate = vacationEnd;
            this.totalStartDate = totalStart;
            this.totalEndDate = totalEnd;
            this.vacationDaysNeeded = vacationDays;
            this.totalDaysOff = totalDays;
            this.efficiency = vacationDays > 0 ? (double) totalDays / vacationDays : 0.0;
            this.description = desc;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            VacationOptimization that = (VacationOptimization) obj;
            return vacationStartDate.equals(that.vacationStartDate) &&
                   vacationEndDate.equals(that.vacationEndDate) &&
                   totalStartDate.equals(that.totalStartDate) &&
                   totalEndDate.equals(that.totalEndDate);
        }
        
        @Override
        public int hashCode() {
            return vacationStartDate.hashCode() + vacationEndDate.hashCode() + 
                   totalStartDate.hashCode() + totalEndDate.hashCode();
        }
    }

    private List<VacationOptimization> findOptimalVacationPeriods(List<HolidayDefinition> holidays, int year, int maxVacationDays) {
        List<VacationOptimization> optimizations = new ArrayList<>();
        
        // Convert holidays to LocalDate set for quick lookup
        Set<LocalDate> holidayDates = holidays.stream()
            .map(HolidayDefinition::getHolidayDate)
            .collect(Collectors.toSet());
        
        // Analyze each holiday and nearby periods
        for (HolidayDefinition holiday : holidays) {
            LocalDate holidayDate = holiday.getHolidayDate();
            
            // Skip if holiday is on weekend (less optimization potential)
            if (isWeekend(holidayDate)) {
                continue;
            }
            
            // Try different vacation day combinations around this holiday
            for (int vacationDays = 1; vacationDays <= maxVacationDays; vacationDays++) {
                // Try extending before the holiday
                VacationOptimization beforeOpt = analyzeVacationBefore(holidayDate, holidayDates, vacationDays, holiday.getTemplate().getDefaultName());
                if (beforeOpt != null) optimizations.add(beforeOpt);
                
                // Try extending after the holiday
                VacationOptimization afterOpt = analyzeVacationAfter(holidayDate, holidayDates, vacationDays, holiday.getTemplate().getDefaultName());
                if (afterOpt != null) optimizations.add(afterOpt);
                
                // Try bridging holidays (if there are multiple holidays close together)
                VacationOptimization bridgeOpt = analyzeBridgeVacation(holidayDate, holidayDates, vacationDays, holidays);
                if (bridgeOpt != null) optimizations.add(bridgeOpt);
            }
        }
        
        // Sort by efficiency (most days off per vacation day used)
        optimizations.sort((a, b) -> Double.compare(b.efficiency, a.efficiency));
        
        // Remove duplicates and low-efficiency options
        return optimizations.stream()
            .filter(opt -> opt.vacationDaysNeeded > 0) // Must require at least 1 vacation day
            .filter(opt -> opt.efficiency >= 1.5) // At least 1.5 days off per vacation day
            .filter(opt -> opt.totalDaysOff >= 4) // At least 4 total days off to be worthwhile
            .distinct()
            .limit(5) // Limit to top 5 options
            .collect(Collectors.toList());
    }

    private VacationOptimization analyzeVacationBefore(LocalDate holidayDate, Set<LocalDate> holidayDates, int vacationDays, String holidayName) {
        LocalDate vacationStart = holidayDate.minusDays(vacationDays);
        LocalDate vacationEnd = holidayDate.minusDays(1);
        
        // Make sure vacation days are actually working days
        int actualVacationDays = 0;
        for (LocalDate date = vacationStart; !date.isAfter(vacationEnd); date = date.plusDays(1)) {
            if (!isWeekend(date) && !holidayDates.contains(date)) {
                actualVacationDays++;
            }
        }
        
        if (actualVacationDays == 0) {
            return null; // No working days to take as vacation
        }
        
        // Find the actual start of the extended period (include previous weekend)
        LocalDate totalStart = vacationStart;
        while (totalStart.getDayOfWeek().getValue() != 1 && !totalStart.equals(vacationStart.minusDays(7))) {
            totalStart = totalStart.minusDays(1);
            if (!isWeekend(totalStart) && !holidayDates.contains(totalStart)) {
                break; // Stop if we hit a working day that's not a holiday
            }
        }
        
        // Find the end of the holiday period
        LocalDate totalEnd = holidayDate;
        while (isWeekend(totalEnd.plusDays(1)) || holidayDates.contains(totalEnd.plusDays(1))) {
            totalEnd = totalEnd.plusDays(1);
        }
        
        int totalDaysOff = (int) totalStart.until(totalEnd).getDays() + 1;
        
        if (totalDaysOff > actualVacationDays + 1) { // Only worthwhile if we get more than just vacation + holiday
            return new VacationOptimization(vacationStart, vacationEnd, totalStart, totalEnd, 
                actualVacationDays, totalDaysOff, 
                String.format("Weekend + %d vacation days + %s", actualVacationDays, holidayName));
        }
        
        return null;
    }

    private VacationOptimization analyzeVacationAfter(LocalDate holidayDate, Set<LocalDate> holidayDates, int vacationDays, String holidayName) {
        LocalDate vacationStart = holidayDate.plusDays(1);
        LocalDate vacationEnd = holidayDate.plusDays(vacationDays);
        
        // Make sure vacation days are actually working days
        int actualVacationDays = 0;
        for (LocalDate date = vacationStart; !date.isAfter(vacationEnd); date = date.plusDays(1)) {
            if (!isWeekend(date) && !holidayDates.contains(date)) {
                actualVacationDays++;
            }
        }
        
        if (actualVacationDays == 0) {
            return null; // No working days to take as vacation
        }
        
        // Find the actual start (include previous weekend before holiday)
        LocalDate totalStart = holidayDate;
        while (isWeekend(totalStart.minusDays(1)) || holidayDates.contains(totalStart.minusDays(1))) {
            totalStart = totalStart.minusDays(1);
        }
        
        // Find the end including following weekend
        LocalDate totalEnd = vacationEnd;
        while (isWeekend(totalEnd.plusDays(1))) {
            totalEnd = totalEnd.plusDays(1);
        }
        
        int totalDaysOff = (int) totalStart.until(totalEnd).getDays() + 1;
        
        if (totalDaysOff > actualVacationDays + 1) {
            return new VacationOptimization(vacationStart, vacationEnd, totalStart, totalEnd, 
                actualVacationDays, totalDaysOff, 
                String.format("%s + %d vacation days + Weekend", holidayName, actualVacationDays));
        }
        
        return null;
    }

    private VacationOptimization analyzeBridgeVacation(LocalDate holidayDate, Set<LocalDate> holidayDates, int vacationDays, List<HolidayDefinition> allHolidays) {
        // Look for another holiday within a reasonable range (2-10 days)
        for (int gap = 2; gap <= 10; gap++) {
            LocalDate nextDate = holidayDate.plusDays(gap);
            if (holidayDates.contains(nextDate) && !nextDate.equals(holidayDate)) {
                // Found another holiday, see if we can bridge them
                
                // Count non-weekend bridge days
                int workingBridgeDays = 0;
                for (int i = 1; i < gap; i++) {
                    LocalDate bridgeDay = holidayDate.plusDays(i);
                    if (!isWeekend(bridgeDay) && !holidayDates.contains(bridgeDay)) {
                        workingBridgeDays++;
                    }
                }
                
                if (workingBridgeDays > 0 && workingBridgeDays <= vacationDays) {
                    // We can bridge these holidays
                    LocalDate totalStart = holidayDate;
                    LocalDate totalEnd = nextDate;
                    
                    // Extend to include surrounding weekends
                    while (isWeekend(totalStart.minusDays(1))) {
                        totalStart = totalStart.minusDays(1);
                    }
                    while (isWeekend(totalEnd.plusDays(1))) {
                        totalEnd = totalEnd.plusDays(1);
                    }
                    
                    int totalDaysOff = (int) totalStart.until(totalEnd).getDays() + 1;
                    
                    String currentHolidayName = allHolidays.stream()
                        .filter(h -> h.getHolidayDate().equals(holidayDate))
                        .map(h -> h.getTemplate().getDefaultName())
                        .findFirst().orElse("Holiday");
                    
                    String nextHolidayName = allHolidays.stream()
                        .filter(h -> h.getHolidayDate().equals(nextDate))
                        .map(h -> h.getTemplate().getDefaultName())
                        .findFirst().orElse("Holiday");
                    
                    // Only return if the holidays are actually different
                    if (!currentHolidayName.equals(nextHolidayName)) {
                        return new VacationOptimization(holidayDate.plusDays(1), nextDate.minusDays(1), 
                            totalStart, totalEnd, workingBridgeDays, totalDaysOff,
                            String.format("Bridge between %s and %s", currentHolidayName, nextHolidayName));
                    }
                }
            }
        }
        
        return null;
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().getValue() >= 6; // Saturday = 6, Sunday = 7
    }

    private int extractMaxVacationDays(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Look for "maximum X days" pattern first
        Pattern maxPattern = Pattern.compile("maximum\\s+(\\d+)\\s*(?:vacation\\s+)?(?:day|days|gün)");
        Matcher matcher = maxPattern.matcher(lowerMessage);
        
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                // Ignore and continue checking other patterns
            }
        }
        
        // Look for patterns like "5 days", "3 vacation days", etc.
        Pattern daysPattern = Pattern.compile("(\\d+)\\s*(?:vacation\\s+)?(?:day|days|gün)");
        matcher = daysPattern.matcher(lowerMessage);
        
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                // Ignore and continue checking other patterns
            }
        }
        
        // Look for "I can take X days" patterns
        Pattern canTakePattern = Pattern.compile("(?:i can take|kullanabilirim)\\s+(\\d+)");
        matcher = canTakePattern.matcher(lowerMessage);
        
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                // Ignore and continue
            }
        }
        
        // Look for "X izin günü" (Turkish pattern)
        Pattern turkishPattern = Pattern.compile("(\\d+)\\s*izin\\s*gün");
        matcher = turkishPattern.matcher(lowerMessage);
        
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                // Ignore and continue
            }
        }
        
        // Look for "with X days" pattern
        Pattern withPattern = Pattern.compile("with\\s+(\\d+)\\s*(?:vacation\\s+)?(?:day|days|gün)");
        matcher = withPattern.matcher(lowerMessage);
        
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                // Ignore and continue
            }
        }
        
        // Return -1 if no vacation days specified (to trigger asking the user)
        return -1;
    }

    // Helper methods for new functionality

    private String extractHolidayNameFromMessage(String message, String language) {
        // Simple extraction - look for quoted strings or common patterns
        Pattern quotedPattern = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = quotedPattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Look for "when is X" pattern
        Pattern whenIsPattern = Pattern.compile("when is ([a-zA-ZığüşöçĞÜŞÖÇ\\s]+)");
        matcher = whenIsPattern.matcher(message.toLowerCase());
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // Look for "tell me about X" pattern
        Pattern tellMePattern = Pattern.compile("tell me about ([a-zA-ZığüşöçĞÜŞÖÇ\\s]+)");
        matcher = tellMePattern.matcher(message.toLowerCase());
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // Look for "how long does X last" pattern
        Pattern howLongPattern = Pattern.compile("how long does ([a-zA-ZığüşöçĞÜŞÖÇ\\s]+) last");
        matcher = howLongPattern.matcher(message.toLowerCase());
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // Look for "X kaç gün" pattern (Turkish)
        Pattern turkishDurationPattern = Pattern.compile("([a-zA-ZığüşöçĞÜŞÖÇ\\s]+) kaç gün");
        matcher = turkishDurationPattern.matcher(message.toLowerCase());
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // Common holiday names that might appear without specific patterns
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("ramazan")) return "ramazan";
        if (lowerMessage.contains("bayram")) return "bayram";
        if (lowerMessage.contains("kurban")) return "kurban";
        if (lowerMessage.contains("christmas")) return "christmas";
        if (lowerMessage.contains("easter")) return "easter";
        if (lowerMessage.contains("new year")) return "new year";
        if (lowerMessage.contains("republic")) return "republic";
        if (lowerMessage.contains("victory")) return "victory";
        if (lowerMessage.contains("independence")) return "independence";
        if (lowerMessage.contains("labour")) return "labour";
        if (lowerMessage.contains("atatürk") || lowerMessage.contains("ataturk")) return "atatürk";
        
        return null;
    }

    private int extractYearFromMessage(String message) {
        Pattern yearPattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        Matcher matcher = yearPattern.matcher(message);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        
        if (message.toLowerCase().contains("next year")) {
            return LocalDate.now().getYear() + 1;
        }
        
        return -1; // Current year
    }

    private String analyzeDuration(HolidayDefinition holiday, String countryCode) {
        // Enhanced duration analysis
        String holidayName = holiday.getTemplate().getDefaultName().toLowerCase();
        
        // Check for known multi-day holidays
        if (holidayName.contains("ramazan") || holidayName.contains("eid al-fitr") || 
            holidayName.contains("bayram")) {
            return " (lasts for 3 days)";
        } else if (holidayName.contains("kurban") || holidayName.contains("eid al-adha")) {
            return " (lasts for 4 days)";
        } else if (holidayName.contains("christmas")) {
            return " (typically celebrated for 2 days)";
        }
        
        // Check if holiday falls on weekend and extends
        int dayOfWeek = holiday.getHolidayDate().getDayOfWeek().getValue();
        if (dayOfWeek == 6 || dayOfWeek == 7) { // Saturday or Sunday
            return " (falls on weekend)";
        } else if (dayOfWeek == 5) { // Friday
            return " (creates long weekend)";
        } else if (dayOfWeek == 1) { // Monday  
            return " (creates long weekend)";
        }
        
        return "";
    }

    private String analyzeDuration(String holidayName, String language) {
        // This could be expanded with a more comprehensive database
        String lowerName = holidayName.toLowerCase();
        
        // Religious holidays with variable durations
        if (lowerName.contains("ramazan") || lowerName.contains("ramadan") || lowerName.contains("eid al-fitr")) {
            if (language.equals("tr")) {
                return "Ramazan Bayramı genellikle 3 gün sürer (resmi olarak), ancak bazı yıllarda 4 gün olabilir. Bayram, ayın görülmesine göre belirlenir.";
            } else {
                return "Ramadan Bayram (Eid al-Fitr) typically lasts 3 days (officially), but can be 4 days in some years. The exact dates depend on moon sighting.";
            }
        }
        
        if (lowerName.contains("kurban") || lowerName.contains("sacrifice") || lowerName.contains("eid al-adha")) {
            if (language.equals("tr")) {
                return "Kurban Bayramı genellikle 4 gün sürer (resmi olarak), ancak bazı yıllarda 3 gün olabilir. Bayram, ayın görülmesine göre belirlenir.";
            } else {
                return "Eid al-Adha (Kurban Bayramı) typically lasts 4 days (officially), but can be 3 days in some years. The exact dates depend on moon sighting.";
            }
        }
        
        // Multi-day holidays
        if (lowerName.contains("new year")) {
            if (language.equals("tr")) {
                return "Yeni Yıl genellikle 1 gün resmi tatil olarak kutlanır (1 Ocak).";
            } else {
                return "New Year is typically celebrated as a 1-day official holiday (January 1st).";
            }
        }
        
        if (lowerName.contains("christmas")) {
            if (language.equals("tr")) {
                return "Noel, Hristiyan ülkelerde genellikle 1-2 gün resmi tatil olarak kutlanır.";
            } else {
                return "Christmas is typically celebrated as 1-2 days of official holidays in Christian countries.";
            }
        }
        
        if (lowerName.contains("easter")) {
            if (language.equals("tr")) {
                return "Paskalya, Hristiyan ülkelerde genellikle Cuma'dan Pazartesi'ye kadar 4 gün sürer.";
            } else {
                return "Easter typically lasts 4 days from Good Friday to Easter Monday in Christian countries.";
            }
        }
        
        // Turkish national holidays (most are single day)
        if (lowerName.contains("republic") || lowerName.contains("cumhuriyet")) {
            if (language.equals("tr")) {
                return "Cumhuriyet Bayramı 29 Ekim'de 1 gün resmi tatil olarak kutlanır.";
            } else {
                return "Republic Day is celebrated as a 1-day official holiday on October 29th.";
            }
        }
        
        if (lowerName.contains("victory") || lowerName.contains("zafer")) {
            if (language.equals("tr")) {
                return "Zafer Bayramı 30 Ağustos'ta 1 gün resmi tatil olarak kutlanır.";
            } else {
                return "Victory Day is celebrated as a 1-day official holiday on August 30th.";
            }
        }
        
        if (lowerName.contains("atatürk") || lowerName.contains("ataturk")) {
            if (language.equals("tr")) {
                return "Atatürk'ü Anma, Gençlik ve Spor Bayramı 19 Mayıs'ta 1 gün resmi tatil olarak kutlanır.";
            } else {
                return "Atatürk Commemoration, Youth and Sports Day is celebrated as a 1-day official holiday on May 19th.";
            }
        }
        
        if (lowerName.contains("independence") || lowerName.contains("bağımsızlık")) {
            if (language.equals("tr")) {
                return "Ulusal Egemenlik ve Çocuk Bayramı 23 Nisan'da 1 gün resmi tatil olarak kutlanır.";
            } else {
                return "National Sovereignty and Children's Day is celebrated as a 1-day official holiday on April 23rd.";
            }
        }
        
        if (lowerName.contains("labour") || lowerName.contains("labor") || lowerName.contains("işçi")) {
            if (language.equals("tr")) {
                return "İşçi Bayramı 1 Mayıs'ta 1 gün resmi tatil olarak kutlanır.";
            } else {
                return "Labour Day is celebrated as a 1-day official holiday on May 1st.";
            }
        }
        
        // Default response for other holidays
        if (language.equals("tr")) {
            return "Bu tatil hakkında detaylı süre bilgisi bulunmuyor. Çoğu resmi tatil 1 gün sürer, dini tatiller ise genellikle 3-4 gün sürer.";
        } else {
            return "Detailed duration information for this holiday is not available. Most official holidays last 1 day, while religious holidays typically last 3-4 days.";
        }
    }

    private boolean containsMonthNames(String message) {
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("january") || lowerMessage.contains("february") || 
               lowerMessage.contains("march") || lowerMessage.contains("april") || 
               lowerMessage.contains("may") || lowerMessage.contains("june") || 
               lowerMessage.contains("july") || lowerMessage.contains("august") || 
               lowerMessage.contains("september") || lowerMessage.contains("october") || 
               lowerMessage.contains("november") || lowerMessage.contains("december") ||
               lowerMessage.contains("ocak") || lowerMessage.contains("şubat") || 
               lowerMessage.contains("mart") || lowerMessage.contains("nisan") || 
               lowerMessage.contains("mayıs") || lowerMessage.contains("haziran") || 
               lowerMessage.contains("temmuz") || lowerMessage.contains("ağustos") || 
               lowerMessage.contains("eylül") || lowerMessage.contains("ekim") || 
               lowerMessage.contains("kasım") || lowerMessage.contains("aralık");
    }

    private int[] extractMonthRange(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Map month names to numbers
        Map<String, Integer> monthMap = Map.ofEntries(
            Map.entry("january", 1), Map.entry("ocak", 1),
            Map.entry("february", 2), Map.entry("şubat", 2),
            Map.entry("march", 3), Map.entry("mart", 3),
            Map.entry("april", 4), Map.entry("nisan", 4),
            Map.entry("may", 5), Map.entry("mayıs", 5),
            Map.entry("june", 6), Map.entry("haziran", 6),
            Map.entry("july", 7), Map.entry("temmuz", 7),
            Map.entry("august", 8), Map.entry("ağustos", 8),
            Map.entry("september", 9), Map.entry("eylül", 9),
            Map.entry("october", 10), Map.entry("ekim", 10),
            Map.entry("november", 11), Map.entry("kasım", 11),
            Map.entry("december", 12), Map.entry("aralık", 12)
        );
        
        List<Integer> foundMonths = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : monthMap.entrySet()) {
            if (lowerMessage.contains(entry.getKey())) {
                foundMonths.add(entry.getValue());
            }
        }
        
        if (foundMonths.size() >= 2) {
            foundMonths.sort(Integer::compareTo);
            return new int[]{foundMonths.get(0), foundMonths.get(foundMonths.size() - 1)};
        } else if (foundMonths.size() == 1) {
            // Single month specified
            int month = foundMonths.get(0);
            return new int[]{month, month};
        }
        
        return null;
    }

    private List<HolidayDefinition> filterHolidaysByType(List<HolidayDefinition> holidays, String requestedType, String language) {
        if (requestedType == null) {
            return holidays;
        }
        
        return holidays.stream()
            .filter(holiday -> isHolidayOfType(holiday, requestedType, language))
            .collect(Collectors.toList());
    }

    private boolean isHolidayOfType(HolidayDefinition holiday, String requestedType, String language) {
        String holidayName = getHolidayName(holiday.getTemplate(), language).toLowerCase();
        String templateType = holiday.getTemplate().getType();
        
        // Check template type first if available
        if (templateType != null && templateType.equalsIgnoreCase(requestedType)) {
            return true;
        }
        
        // Fallback to name-based classification
        switch (requestedType.toUpperCase()) {
            case "RELIGIOUS":
                return holidayName.contains("eid") || holidayName.contains("bayram") || 
                       holidayName.contains("ramazan") || holidayName.contains("kurban") ||
                       holidayName.contains("christmas") || holidayName.contains("easter") ||
                       holidayName.contains("religious") || holidayName.contains("dini") ||
                       holidayName.contains("mevlid") || holidayName.contains("kandil");
                       
            case "OFFICIAL":
            case "PUBLIC":
                return holidayName.contains("republic") || holidayName.contains("cumhuriyet") ||
                       holidayName.contains("independence") || holidayName.contains("bağımsızlık") ||
                       holidayName.contains("national") || holidayName.contains("ulusal") ||
                       holidayName.contains("victory") || holidayName.contains("zafer") ||
                       holidayName.contains("labour") || holidayName.contains("işçi") ||
                       holidayName.contains("new year") || holidayName.contains("yılbaşı");
                       
            case "CULTURAL":
                return holidayName.contains("children") || holidayName.contains("çocuk") ||
                       holidayName.contains("youth") || holidayName.contains("gençlik") ||
                       holidayName.contains("women") || holidayName.contains("kadın") ||
                       holidayName.contains("mother") || holidayName.contains("anne") ||
                       holidayName.contains("father") || holidayName.contains("baba");
                       
            case "NATIONAL":
                return holidayName.contains("atatürk") || holidayName.contains("ataturk") ||
                       holidayName.contains("sovereignty") || holidayName.contains("egemenlik") ||
                       holidayName.contains("democracy") || holidayName.contains("demokrasi") ||
                       holidayName.contains("memorial") || holidayName.contains("anma");
                       
            default:
                return true; // Include all if type not recognized
        }
    }

    private String analyzeMonthlyDistribution(List<HolidayDefinition> holidays, String language, String countryCode) {
        Map<Integer, Long> monthCount = holidays.stream()
            .collect(Collectors.groupingBy(
                h -> h.getHolidayDate().getMonthValue(),
                Collectors.counting()
            ));

        int maxMonth = monthCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(1);

        String monthName = LocalDate.of(2024, maxMonth, 1)
            .format(DateTimeFormatter.ofPattern("MMMM"));

        return getLocalizedMessage(language,
            String.format("%s has the most holidays in %s with %d holidays.", 
                monthName, getCountryName(countryCode), monthCount.get(maxMonth)),
            String.format("%s'de en çok tatil %s ayında var: %d tatil.", 
                getCountryName(countryCode), monthName, monthCount.get(maxMonth)));
    }

    private String analyzeLongestHoliday(List<HolidayDefinition> holidays, String language, String countryCode) {
        // For now, just return the first holiday as "longest"
        // This would need enhancement to track multi-day holidays
        if (holidays.isEmpty()) {
            return getLocalizedMessage(language, "No holidays found.", "Tatil bulunamadı.");
        }
        
        HolidayDefinition firstHoliday = holidays.get(0);
        return getLocalizedMessage(language,
            String.format("The longest holiday period includes %s on %s.", 
                getHolidayName(firstHoliday.getTemplate(), language),
                firstHoliday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
            String.format("En uzun tatil dönemi %s tarihindeki %s'i içerir.", 
                firstHoliday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                getHolidayName(firstHoliday.getTemplate(), language)));
    }

    private String analyzeWeekendHolidays(List<HolidayDefinition> holidays, String language, String countryCode) {
        long weekendHolidays = holidays.stream()
            .filter(h -> h.getHolidayDate().getDayOfWeek().getValue() >= 6)
            .count();

        return getLocalizedMessage(language,
            String.format("%d holidays fall on weekends in %s this year.", 
                weekendHolidays, getCountryName(countryCode)),
            String.format("Bu yıl %s'de %d tatil hafta sonuna denk geliyor.", 
                getCountryName(countryCode), weekendHolidays));
    }

    private String generateGeneralStatistics(List<HolidayDefinition> holidays, String language, String countryCode, int year) {
        long weekendHolidays = holidays.stream()
            .filter(h -> h.getHolidayDate().getDayOfWeek().getValue() >= 6)
            .count();

        return getLocalizedMessage(language,
            String.format("Holiday statistics for %s in %d:\n" +
                "• Total holidays: %d\n" +
                "• Holidays on weekends: %d\n" +
                "• Average holidays per month: %.1f",
                getCountryName(countryCode), year, holidays.size(), 
                weekendHolidays, holidays.size() / 12.0),
            String.format("%d yılında %s tatil istatistikleri:\n" +
                "• Toplam tatil: %d\n" +
                "• Hafta sonu tatilleri: %d\n" +
                "• Aylık ortalama tatil: %.1f",
                year, getCountryName(countryCode), holidays.size(), 
                weekendHolidays, holidays.size() / 12.0));
    }

    private String getLocalizedMessage(String language, String englishMessage, String turkishMessage) {
        return "tr".equalsIgnoreCase(language) ? turkishMessage : englishMessage;
    }

    private String getLocalizedErrorMessage(String language) {
        return getLocalizedMessage(language,
            "I'm sorry, I encountered an error while processing your request. Please try again or rephrase your question.",
            "Üzgünüm, isteğinizi işlerken bir hata oluştu. Lütfen tekrar deneyin veya sorunuzu yeniden ifade edin.");
    }
}
