package com.emre.holidayapi.service;

import com.emre.holidayapi.model.HolidayTemplate;
import com.emre.holidayapi.repository.HolidayTemplateRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HolidayTemplateService {
    private final HolidayTemplateRepository repository;

    public HolidayTemplateService(HolidayTemplateRepository repository) {
        this.repository = repository;
    }

    public List<HolidayTemplate> getAllTemplates() {
        return repository.findAll();
    }

    public HolidayTemplate createTemplate(HolidayTemplate template) {
        return repository.save(template);
    }

    public HolidayTemplate findByCode(String code) {
        return repository.findByCode(code);
    }
}