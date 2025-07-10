package main.java.com.emre.holidayapi.controller;

import com.emre.holidayapi.entity.Country;
import com.emre.holidayapi.service.CountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryService service;

    public CountryController(CountryService service) {
        this.service = service;
    }

    @GetMapping
    public List<Country> getAll() {
        return service.findAll();
    }

    @GetMapping("/{countryCode}")
    public ResponseEntity<Country> getByCode(@PathVariable String countryCode) {
        return service.findByCode(countryCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Country create(@RequestBody Country country) {
        return service.save(country);
    }

    @PutMapping("/{countryCode}")
    public ResponseEntity<Country> update(@PathVariable String countryCode, @RequestBody Country updatedCountry) {
        return service.findByCode(countryCode)
                .map(existing -> {
                    existing.setName(updatedCountry.getName());
                    return ResponseEntity.ok(service.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{countryCode}")
    public ResponseEntity<Void> delete(@PathVariable String countryCode) {
        if (service.findByCode(countryCode).isPresent()) {
            service.deleteByCode(countryCode);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
