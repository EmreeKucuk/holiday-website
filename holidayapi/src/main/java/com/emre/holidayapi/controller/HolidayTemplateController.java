package com.emre.holidayapi.controller;

import com.emre.holidayapi.model.HolidayTemplate;
import com.emre.holidayapi.service.HolidayTemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holiday-templates")
public class HolidayTemplateController {
    private final HolidayTemplateService service;

    public HolidayTemplateController(HolidayTemplateService service) {
        this.service = service;
    }

    @GetMapping
    public List<HolidayTemplate> getAllTemplates() {
        return service.getAllTemplates();
    }
}

