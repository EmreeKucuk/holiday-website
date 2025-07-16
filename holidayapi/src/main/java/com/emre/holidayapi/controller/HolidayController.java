package com.emre.holidayapi.controller;

import com.emre.holidayapi.model.*;
import com.emre.holidayapi.service.HolidayService;
import com.emre.holidayapi.service.AudienceService;
import com.emre.holidayapi.dto.HolidayDto;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    private final HolidayService holidayService;
    private final AudienceService audienceService;

    public HolidayController(HolidayService holidayService, AudienceService audienceService) {
        this.holidayService = holidayService;
        this.audienceService = audienceService;
    }

    @GetMapping
    public List<HolidayDefinition> getAllHolidays() {
        return holidayService.getAllHolidays();
    }

    @GetMapping("/country/{countryCode}")
    public List<HolidayDto> getHolidaysByCountry(@PathVariable String countryCode) {
        List<HolidayDefinition> defs = holidayService.getHolidaysByCountry(countryCode);
        return defs.stream().map(def -> {
            HolidayDto dto = new HolidayDto();
            dto.name = def.getTemplate().getDefaultName();
            dto.date = def.getHolidayDate().toString();
            dto.countryCode = countryCode;
            dto.type = def.getTemplate().getType();
            dto.audiences = getAudiencesForHoliday(def.getTemplate().getCode());
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
        @RequestParam(required = false) String audience
    ) {
        LocalDate today = LocalDate.now();
        List<HolidayDefinition> defs;
        // For now, audience filtering is temporarily disabled until proper schema is implemented
        // if (audience != null && !audience.isEmpty()) {
        //     defs = holidayService.getHolidaysByCountryDateRangeAndAudience(country, today, today, audience);
        // } else {
            defs = holidayService.getHolidaysByCountryAndDateRange(country, today, today);
        // }
        return defs.stream().map(def -> {
            HolidayDto dto = new HolidayDto();
            dto.name = def.getTemplate().getDefaultName();
            dto.date = def.getHolidayDate().toString();
            dto.countryCode = country;
            dto.type = def.getTemplate().getType();
            dto.audiences = getAudiencesForHoliday(def.getTemplate().getCode());
            return dto;
        }).toList();
    }

    @GetMapping("/range")
    public List<HolidayDto> getHolidaysInRange(
        @RequestParam String start,
        @RequestParam String end,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String audience
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<HolidayDefinition> defs;
        if (country != null && !country.isEmpty()) {
            // For now, audience filtering is temporarily disabled until proper schema is implemented
            // if (audience != null && !audience.isEmpty()) {
            //     defs = holidayService.getHolidaysByCountryDateRangeAndAudience(country, startDate, endDate, audience);
            // } else {
                defs = holidayService.getHolidaysByCountryAndDateRange(country, startDate, endDate);
            // }
        } else {
            defs = holidayService.getHolidaysInRange(startDate, endDate);
        }
        return defs.stream().map(def -> {
            HolidayDto dto = new HolidayDto();
            dto.name = def.getTemplate().getDefaultName();
            dto.date = def.getHolidayDate().toString();
            dto.countryCode = country != null ? country : def.getTemplate().getCode();
            dto.type = def.getTemplate().getType();
            dto.audiences = getAudiencesForHoliday(def.getTemplate().getCode());
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
}