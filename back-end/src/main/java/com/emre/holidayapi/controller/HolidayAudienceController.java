package main.java.com.emre.holidayapi.controller;

import com.emre.holidayapi.entity.HolidayAudience;
import com.emre.holidayapi.service.HolidayAudienceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holiday-audience")
public class HolidayAudienceController {

    private final HolidayAudienceService service;

    public HolidayAudienceController(HolidayAudienceService service) {
        this.service = service;
    }

    @GetMapping
    public List<HolidayAudience> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HolidayAudience> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public HolidayAudience create(@RequestBody HolidayAudience holidayAudience) {
        return service.save(holidayAudience);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HolidayAudience> update(@PathVariable Long id, @RequestBody HolidayAudience updatedAudience) {
        return service.findById(id)
                .map(existing -> {
                    existing.setAudience(updatedAudience.getAudience());
                    existing.setHolidayDefinition(updatedAudience.getHolidayDefinition());
                    return ResponseEntity.ok(service.save(existing));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
