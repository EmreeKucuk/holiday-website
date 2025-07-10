package main.java.com.emre.holidayapi.controller;

import com.emre.holidayapi.entity.Audience;
import com.emre.holidayapi.service.AudienceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audience")
public class AudienceController {

    private final AudienceService service;

    public AudienceController(AudienceService service) {
        this.service = service;
    }

    @GetMapping
    public List<Audience> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Audience> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Audience create(@RequestBody Audience audience) {
        return service.save(audience);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Audience> update(@PathVariable Long id, @RequestBody Audience updatedAudience) {
        return service.findById(id)
                .map(existing -> {
                    existing.setAudienceCode(updatedAudience.getAudienceCode());
                    existing.setDescription(updatedAudience.getDescription());
                    return ResponseEntity.ok(service.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
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
