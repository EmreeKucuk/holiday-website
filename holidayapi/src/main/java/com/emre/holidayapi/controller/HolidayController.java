package com.emre.holidayapi.controller;

import com.emre.holidayapi.model.*;
import com.emre.holidayapi.service.HolidayService;
import com.emre.holidayapi.service.AudienceService;
import com.emre.holidayapi.repository.TranslationRepository;
import com.emre.holidayapi.repository.HolidayTemplateRepository;
import com.emre.holidayapi.dto.HolidayDto;
import com.emre.holidayapi.dto.AudienceDto;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.time.LocalDate;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    private final HolidayService holidayService;
    private final AudienceService audienceService;
    private final TranslationRepository translationRepository;
    private final HolidayTemplateRepository holidayTemplateRepository;

    public HolidayController(HolidayService holidayService, AudienceService audienceService, 
                           TranslationRepository translationRepository, HolidayTemplateRepository holidayTemplateRepository) {
        this.holidayService = holidayService;
        this.audienceService = audienceService;
        this.translationRepository = translationRepository;
        this.holidayTemplateRepository = holidayTemplateRepository;
    }

    @GetMapping
    public List<HolidayDefinition> getAllHolidays() {
        return holidayService.getAllHolidays();
    }

    @GetMapping("/country/{countryCode}")
    public List<HolidayDto> getHolidaysByCountry(
        @PathVariable String countryCode,
        @RequestParam(required = false, defaultValue = "en") String language
    ) {
        List<HolidayDefinition> defs = holidayService.getHolidaysByCountry(countryCode);
        return defs.stream().map(def -> {
            HolidayDto dto = new HolidayDto();
            dto.name = getHolidayName(def.getTemplate(), language);
            dto.date = def.getHolidayDate().toString();
            dto.countryCode = countryCode;
            dto.type = def.getTemplate().getType();
            dto.audiences = getAudiencesForHoliday(def.getTemplate().getId());
            // Set other fields as needed (fixed, global, counties, launchYear)
            return dto;
        }).toList();
    }

    @GetMapping("/country/{countryCode}/year/{year}")
    public List<HolidayDefinition> getHolidaysByCountryAndYear(@PathVariable String countryCode, @PathVariable int year) {
        return holidayService.getHolidaysByCountryAndYear(countryCode, year);
    }

    @GetMapping("/types")
    public List<String> getHolidayTypes() {
        return holidayService.getHolidayTypes();
    }

    @GetMapping("/types/translated")
    public Map<String, String> getTranslatedHolidayTypes(@RequestParam(required = false, defaultValue = "en") String language) {
        Map<String, String> types = new HashMap<>();
        
        if ("tr".equals(language)) {
            types.put("official", "Resmi Tatil");
            types.put("religious", "Dini Tatil");
            types.put("cultural", "Kültürel Tatil");
            types.put("observance", "Anma Günü");
            types.put("national", "Ulusal Tatil");
            types.put("public", "Kamu Tatili");
        } else {
            // Default to English
            types.put("official", "Official Holiday");
            types.put("religious", "Religious Holiday");
            types.put("cultural", "Cultural Holiday");
            types.put("observance", "Observance");
            types.put("national", "National Holiday");
            types.put("public", "Public Holiday");
        }
        
        return types;
    }

    @GetMapping("/workdays")
    public int getWorkDaysBetweenDates(@RequestParam String start, @RequestParam String end) {
        return holidayService.getWorkDaysBetweenDates(start, end);
    }

    @GetMapping("/{id}")
    public HolidayDefinition getHolidayDetails(@PathVariable Long id) {
        return holidayService.getHolidayDetails(id);
    }

    @PostMapping
    public HolidayDefinition createHoliday(@RequestBody HolidayDefinition holiday) {
        return holidayService.createHoliday(holiday);
    }

    @PutMapping("/{id}")
    public HolidayDefinition updateHoliday(@PathVariable Long id, @RequestBody HolidayDefinition holiday) {
        return holidayService.updateHoliday(id, holiday);
    }

    @DeleteMapping("/{id}")
    public void deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
    }

    // Holiday Condition endpoints
    @PostMapping("/{id}/conditions")
    public Object addHolidayCondition(@PathVariable Long id, @RequestBody Object condition) {
        // return holidayService.addHolidayCondition(id, condition);
        return null;
    }

    @PutMapping("/conditions/{conditionId}")
    public Object updateHolidayCondition(@PathVariable Long conditionId, @RequestBody Object condition) {
        // return holidayService.updateHolidayCondition(conditionId, condition);
        return null;
    }

    @DeleteMapping("/conditions/{conditionId}")
    public void deleteHolidayCondition(@PathVariable Long conditionId) {
        // holidayService.deleteHolidayCondition(conditionId);
    }

    // Holiday Spec endpoints
    @PostMapping("/{id}/specs")
    public Object addHolidaySpec(@PathVariable Long id, @RequestBody Object spec) {
        // return holidayService.addHolidaySpec(id, spec);
        return null;
    }

    @PutMapping("/specs/{specId}")
    public Object updateHolidaySpec(@PathVariable Long specId, @RequestBody Object spec) {
        // return holidayService.updateHolidaySpec(specId, spec);
        return null;
    }

    @DeleteMapping("/specs/{specId}")
    public void deleteHolidaySpec(@PathVariable Long specId) {
        // holidayService.deleteHolidaySpec(specId);
    }

    // Audience endpoints
    @GetMapping("/audiences")
    public List<Audience> getAllAudiences() {
        return audienceService.getAllAudiences();
    }

    @GetMapping("/audiences/translated")
    public List<AudienceDto> getTranslatedAudiences(@RequestParam(required = false, defaultValue = "en") String language) {
        List<Audience> audiences = audienceService.getAllAudiences();
        return audiences.stream().map(audience -> {
            String translatedName;
            
            // Simple translation mapping - in a full app, this would be stored in the database
            if ("tr".equals(language)) {
                switch (audience.getCode()) {
                    case "general":
                        translatedName = "Genel Halk";
                        break;
                    case "government":
                        translatedName = "Devlet";
                        break;
                    case "religious":
                        translatedName = "Dini";
                        break;
                    case "educational":
                        translatedName = "Eğitim";
                        break;
                    case "workers":
                        translatedName = "İşçiler";
                        break;
                    case "all":
                        translatedName = "Tüm Çalışanlar";
                        break;
                    case "blue_collar":
                        translatedName = "Mavi Yakalı Çalışanlar";
                        break;
                    default:
                        translatedName = audience.getAudienceName();
                }
            } else {
                // Default to English
                switch (audience.getCode()) {
                    case "general":
                        translatedName = "General Public";
                        break;
                    case "government":
                        translatedName = "Government";
                        break;
                    case "religious":
                        translatedName = "Religious";
                        break;
                    case "educational":
                        translatedName = "Educational";
                        break;
                    case "workers":
                        translatedName = "Workers";
                        break;
                    case "all":
                        translatedName = "All Employees";
                        break;
                    case "blue_collar":
                        translatedName = "Blue Collar Workers";
                        break;
                    default:
                        translatedName = audience.getAudienceName();
                }
            }
            
            return new AudienceDto(audience.getCode(), translatedName);
        }).toList();
    }

    @PostMapping("/audiences")
    public Audience addAudience(@RequestBody Audience audience) {
        return audienceService.addAudience(audience);
    }

    @PutMapping("/audiences/{audienceId}")
    public Audience updateAudience(@PathVariable String audienceId, @RequestBody Audience audience) {
        return audienceService.updateAudience(audienceId, audience);
    }

    @DeleteMapping("/audiences/{audienceId}")
    public void deleteAudience(@PathVariable String audienceId) {
        audienceService.deleteAudience(audienceId);
    }

    @GetMapping("/today")
    public List<HolidayDto> getTodayHolidays(
        @RequestParam String country,
        @RequestParam(required = false) String audience,
        @RequestParam(required = false, defaultValue = "en") String language
    ) {
        LocalDate today = LocalDate.now();
        List<HolidayDefinition> defs;
        // Enable audience filtering for today's holidays
        if (audience != null && !audience.isEmpty()) {
            defs = holidayService.getHolidaysByCountryDateRangeAndAudience(country, today, today, audience);
        } else {
            defs = holidayService.getHolidaysByCountryAndDateRange(country, today, today);
        }
        return defs.stream().map(def -> {
            HolidayDto dto = new HolidayDto();
            dto.name = getHolidayName(def.getTemplate(), language);
            dto.date = def.getHolidayDate().toString();
            dto.countryCode = country;
            dto.type = def.getTemplate().getType();
            dto.audiences = getAudiencesForHoliday(def.getTemplate().getId());
            return dto;
        }).toList();
    }

    @GetMapping("/range")
    public List<HolidayDto> getHolidaysInRange(
        @RequestParam String start,
        @RequestParam String end,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String audience,
        @RequestParam(required = false, defaultValue = "en") String language
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<HolidayDefinition> defs;
        if (country != null && !country.isEmpty()) {
            if (audience != null && !audience.isEmpty()) {
                defs = holidayService.getHolidaysByCountryDateRangeAndAudience(country, startDate, endDate, audience);
            } else {
                defs = holidayService.getHolidaysByCountryAndDateRange(country, startDate, endDate);
            }
        } else {
            defs = holidayService.getHolidaysInRange(startDate, endDate);
        }
        return defs.stream().map(def -> {
            HolidayDto dto = new HolidayDto();
            dto.name = getHolidayName(def.getTemplate(), language);
            dto.date = def.getHolidayDate().toString();
            dto.countryCode = country != null ? country : def.getTemplate().getCode();
            dto.type = def.getTemplate().getType();
            dto.audiences = getAudiencesForHoliday(def.getTemplate().getId());
            return dto;
        }).toList();
    }

    @GetMapping("/debug")
    public Map<String, Object> debugParams(
        @RequestParam(required = false) String start,
        @RequestParam(required = false) String end,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String audience
    ) {
        Map<String, Object> debug = new HashMap<>();
        debug.put("start", start);
        debug.put("end", end);
        debug.put("country", country);
        debug.put("audience", audience);
        debug.put("timestamp", LocalDate.now().toString());
        return debug;
    }

    // @PostMapping("/api/chat")
    // public Map<String, String> chat(@RequestBody Map<String, String> body) {
    //     String userMessage = body.get("message");
    //     String aiReply = springAiService.ask(userMessage); // Use Spring AI here
    //     return Map.of("reply", aiReply);
    // }

    private String getHolidayName(HolidayTemplate template, String language) {
        // Try to get translation from database first
        return translationRepository.findByTemplateIdAndLanguageCode(template.getId(), language)
            .map(Translation::getTranslatedName)
            .orElse(template.getDefaultName()); // Fallback to default name if no translation found
    }

    private List<String> getAudiencesForHoliday(Long templateId) {
        // This method uses template ID instead of holiday code
        // For now, we'll fall back to the holiday code-based method
        // TODO: Implement proper many-to-many relationship between HolidayTemplate and Audience
        
        // We need to get the template first to get its code
        return holidayTemplateRepository.findById(templateId)
            .map(template -> getAudiencesForHoliday(template.getCode()))
            .orElse(List.of("General Public")); // Default fallback
    }

    private List<String> getAudiencesForHoliday(String holidayCode) {
        // This is a temporary implementation. In a real system, this would query 
        // a proper many-to-many relationship table between holidays and audiences.
        // For now, we'll provide some sample audience mappings based on holiday types.
        
        List<String> audiences = new ArrayList<>();
        
        // Example mappings - replace with actual database queries when schema is ready
        switch (holidayCode) {
            case "new_year":
                audiences.add("General Public");
                audiences.add("Government");
                break;
            case "eid_al_fitr":
            case "eid_al_adha":
                audiences.add("General Public");
                audiences.add("Religious");
                break;
            case "national_sovereignty_day":
            case "victory_day":
            case "republic_day":
                audiences.add("General Public");
                audiences.add("Government");
                audiences.add("Educational");
                break;
            case "labour_day":
                audiences.add("General Public");
                audiences.add("Workers");
                break;
            case "ataturk_memorial_day":
            case "democracy_day":
                audiences.add("General Public");
                audiences.add("Government");
                audiences.add("Educational");
                break;
            default:
                audiences.add("General Public");
                break;
        }
        
        return audiences;
    }

    @GetMapping("/working-days")
    public Map<String, Object> calculateWorkingDays(
        @RequestParam String start,
        @RequestParam String end,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String audience,
        @RequestParam(required = false, defaultValue = "en") String language,
        @RequestParam(required = false, defaultValue = "true") boolean includeEndDate
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        
        // Get holidays in the date range
        List<HolidayDefinition> holidays;
        if (country != null && !country.isEmpty()) {
            if (audience != null && !audience.isEmpty()) {
                holidays = holidayService.getHolidaysByCountryDateRangeAndAudience(country, startDate, endDate, audience);
            } else {
                holidays = holidayService.getHolidaysByCountryAndDateRange(country, startDate, endDate);
            }
        } else {
            holidays = holidayService.getHolidaysInRange(startDate, endDate);
        }
        
        // Calculate days - adjust end date based on includeEndDate parameter
        LocalDate calculationEndDate = includeEndDate ? endDate : endDate.minusDays(1);
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, calculationEndDate) + 1;
        
        // Filter holidays to match the calculation range
        List<HolidayDefinition> filteredHolidays = holidays.stream()
            .filter(holiday -> !holiday.getHolidayDate().isAfter(calculationEndDate))
            .collect(java.util.stream.Collectors.toList());
            
        int holidayDays = filteredHolidays.size();
        
        // Calculate working days (excluding weekends and holidays)
        long workingDays = 0;
        LocalDate currentDate = startDate;
        Set<LocalDate> holidayDates = filteredHolidays.stream()
            .map(HolidayDefinition::getHolidayDate)
            .collect(java.util.stream.Collectors.toSet());
            
        while (!currentDate.isAfter(calculationEndDate)) {
            // Check if it's not weekend (Saturday=6, Sunday=7) and not a holiday
            if (currentDate.getDayOfWeek().getValue() < 6 && !holidayDates.contains(currentDate)) {
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }
        
        // Convert filtered holidays to DTOs
        List<HolidayDto> holidayDtos = filteredHolidays.stream().map(def -> {
            HolidayDto dto = new HolidayDto();
            dto.name = getHolidayName(def.getTemplate(), language);
            dto.date = def.getHolidayDate().toString();
            dto.countryCode = country != null ? country : "GLOBAL";
            dto.type = def.getTemplate().getType();
            dto.audiences = getAudiencesForHoliday(def.getTemplate().getId());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
        
        // Create result map
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("totalDays", totalDays);
        result.put("workingDays", workingDays);
        result.put("holidayDays", holidayDays);
        result.put("holidays", holidayDtos);
        
        return result;
    }
}