package main.java.com.emre.holidayapi.service;

import com.emre.holidayapi.model.HolidayTranslation;
import com.emre.holidayapi.repository.HolidayTranslationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HolidayTranslationService {

    private final HolidayTranslationRepository repository;

    public HolidayTranslationService(HolidayTranslationRepository repository) {
        this.repository = repository;
    }

    public List<HolidayTranslation> getAll() {
        return repository.findAll();
    }

    public Optional<HolidayTranslation> getById(Long id) {
        return repository.findById(id);
    }

    public List<HolidayTranslation> getByTemplateId(Long templateId) {
        return repository.findByHolidayTemplateId(templateId);
    }

    public List<HolidayTranslation> getByLanguageCode(String languageCode) {
        return repository.findByLanguageCode(languageCode);
    }

    public HolidayTranslation save(HolidayTranslation translation) {
        return repository.save(translation);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
