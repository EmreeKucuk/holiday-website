package main.java.com.emre.holidayapi.controller;

import com.emre.holidayapi.entity.HolidayDefinition;
import com.emre.holidayapi.service.HolidayDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holiday-definitions")
public class HolidayDefinitionController {

    private final HolidayDefinitionService holidayDefinitionService;

    @Autowired
    public HolidayDefinitionController(HolidayDefinitionService holidayDefinitionService) {
        this.holidayDefinitionService = holidayDefinitionService;
    }

    @GetMapping
    public List<HolidayDefinition> getAll() {
        return holidayDefinitionService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HolidayDefinition> getById(@PathVariable Long id) {
        return holidayDefinitionService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public HolidayDefinition create(@RequestBody HolidayDefinition holidayDefinition) {
        return holidayDefinitionService.save(holidayDefinition);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HolidayDefinition> update(@PathVariable Long id, @RequestBody HolidayDefinition updatedHolidayDefinition) {
        return holidayDefinitionService.getById(id).map(existing -> {
            existing.setHolidayDate(updatedHolidayDefinition.getHolidayDate());
            existing.setTemplate(updatedHolidayDefinition.getTemplate());
            HolidayDefinition saved = holidayDefinitionService.save(existing);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (holidayDefinitionService.getById(id).isPresent()) {
            holidayDefinitionService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
