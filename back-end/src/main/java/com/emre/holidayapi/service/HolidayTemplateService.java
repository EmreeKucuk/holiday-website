package main.java.com.emre.holidayapi.service;

import com.emre.holidayapi.model.HolidayTemplate;
import com.emre.holidayapi.repository.HolidayTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HolidayTemplateService {

    private final HolidayTemplateRepository holidayTemplateRepository;

    public HolidayTemplateService(HolidayTemplateRepository holidayTemplateRepository) {
        this.holidayTemplateRepository = holidayTemplateRepository;
    }

    public List<HolidayTemplate> getAllHolidayTemplates() {
        return holidayTemplateRepository.findAll();
    }

    public Optional<HolidayTemplate> getHolidayTemplateById(Long id) {
        return holidayTemplateRepository.findById(id);
    }

    public HolidayTemplate createHolidayTemplate(HolidayTemplate template) {
        return holidayTemplateRepository.save(template);
    }

    public HolidayTemplate updateHolidayTemplate(Long id, HolidayTemplate updatedTemplate) {
        return holidayTemplateRepository.findById(id)
                .map(template -> {
                    template.setCode(updatedTemplate.getCode());
                    template.setDefaultName(updatedTemplate.getDefaultName());
                    template.setType(updatedTemplate.getType());
                    return holidayTemplateRepository.save(template);
                })
                .orElseThrow(() -> new RuntimeException("HolidayTemplate not found with id " + id));
    }

    public void deleteHolidayTemplate(Long id) {
        holidayTemplateRepository.deleteById(id);
    }
}
