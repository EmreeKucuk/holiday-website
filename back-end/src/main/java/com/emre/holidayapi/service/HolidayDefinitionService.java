package main.java.com.emre.holidayapi.service;

import com.emre.holidayapi.entity.HolidayDefinition;
import com.emre.holidayapi.repository.HolidayDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HolidayDefinitionService {

    private final HolidayDefinitionRepository holidayDefinitionRepository;

    @Autowired
    public HolidayDefinitionService(HolidayDefinitionRepository holidayDefinitionRepository) {
        this.holidayDefinitionRepository = holidayDefinitionRepository;
    }

    public List<HolidayDefinition> getAll() {
        return holidayDefinitionRepository.findAll();
    }

    public Optional<HolidayDefinition> getById(Long id) {
        return holidayDefinitionRepository.findById(id);
    }

    public HolidayDefinition save(HolidayDefinition holidayDefinition) {
        return holidayDefinitionRepository.save(holidayDefinition);
    }

    public void delete(Long id) {
        holidayDefinitionRepository.deleteById(id);
    }
}
