package main.java.com.emre.holidayapi.controller;

import com.emre.holidayapi.model.HolidayTranslation;
import com.emre.holidayapi.service.HolidayTranslationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holiday-translations")
@CrossOrigin(origins = "*")
public class HolidayTranslationController {

    private final HolidayTranslationService service;

    public HolidayTranslationController(HolidayTranslationService service) {
        this.service = service;
    }

    @GetMapping
    public List<HolidayTranslation> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HolidayTranslation> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/template/{templateId}")
    public List<HolidayTranslation> getByTemplateId(@PathVariable Long templateId) {
        return service.getByTemplateId(templateId);
    }

    @GetMapping("/language/{languageCode}")
    public List<HolidayTranslation> getByLanguage(@PathVariable String languageCode) {
        return service.getByLanguageCode(languageCode);
    }

    @PostMapping
    public HolidayTranslation create(@RequestBody HolidayTranslation translation) {
        return service.save(translation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
