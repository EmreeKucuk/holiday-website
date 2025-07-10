package main.java.com.emre.holidayapi.controller;

import com.emre.holidayapi.model.CountryHoliday;
import com.emre.holidayapi.service.CountryHolidayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/country-holidays")
@CrossOrigin(origins = "*")  // React frontend için CORS ayarı
public class CountryHolidayController {

    private final CountryHolidayService service;

    public CountryHolidayController(CountryHolidayService service) {
        this.service = service;
    }

    @GetMapping
    public List<CountryHoliday> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountryHoliday> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/country/{code}")
    public List<CountryHoliday> getByCountryCode(@PathVariable String code) {
        return service.getByCountryCode(code);
    }

    @PostMapping
    public CountryHoliday create(@RequestBody CountryHoliday countryHoliday) {
        return service.save(countryHoliday);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
