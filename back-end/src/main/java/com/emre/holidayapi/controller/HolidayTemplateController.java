package main.java.com.emre.holidayapi.controller;

import com.emre.holidayapi.model.HolidayTemplate;
import com.emre.holidayapi.service.HolidayTemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holiday-templates")
public class HolidayTemplateController {

    private final HolidayTemplateService holidayTemplateService;

    public HolidayTemplateController(HolidayTemplateService holidayTemplateService) {
        this.holidayTemplateService = holidayTemplateService;
    }

    // Tüm tatil şablonlarını listele
    @GetMapping
    public ResponseEntity<List<HolidayTemplate>> getAllHolidayTemplates() {
        List<HolidayTemplate> templates = holidayTemplateService.getAllHolidayTemplates();
        return ResponseEntity.ok(templates);
    }

    // ID ile tatil şablonu getir
    @GetMapping("/{id}")
    public ResponseEntity<HolidayTemplate> getHolidayTemplateById(@PathVariable Long id) {
        return holidayTemplateService.getHolidayTemplateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Yeni tatil şablonu oluştur
    @PostMapping
    public ResponseEntity<HolidayTemplate> createHolidayTemplate(@RequestBody HolidayTemplate template) {
        HolidayTemplate createdTemplate = holidayTemplateService.createHolidayTemplate(template);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);
    }

    // Tatil şablonunu güncelle
    @PutMapping("/{id}")
    public ResponseEntity<HolidayTemplate> updateHolidayTemplate(
            @PathVariable Long id,
            @RequestBody HolidayTemplate updatedTemplate) {
        try {
            HolidayTemplate template = holidayTemplateService.updateHolidayTemplate(id, updatedTemplate);
            return ResponseEntity.ok(template);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Tatil şablonunu sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHolidayTemplate(@PathVariable Long id) {
        holidayTemplateService.deleteHolidayTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
