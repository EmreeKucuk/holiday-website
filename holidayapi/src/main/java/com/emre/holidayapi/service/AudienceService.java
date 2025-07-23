package com.emre.holidayapi.service;

import com.emre.holidayapi.model.Audience;
import com.emre.holidayapi.dto.AudienceDto;
import com.emre.holidayapi.repository.AudienceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AudienceService {
    private final AudienceRepository repository;

    public AudienceService(AudienceRepository repository) {
        this.repository = repository;
    }

    public List<Audience> getAllAudiences() {
        return repository.findAll();
    }

    public Audience addAudience(Audience audience) {
        // Check if audience with the same code already exists
        if (repository.existsById(audience.getCode())) {
            throw new IllegalArgumentException("Audience with code " + audience.getCode() + " already exists.");
        }
        return repository.save(audience);
    }

    public Audience updateAudience(String audienceId, Audience audience) {
        // Check if audience exists
        Optional<Audience> existingAudience = repository.findById(audienceId);
        if (!existingAudience.isPresent()) {
            throw new IllegalArgumentException("Audience with id " + audienceId + " does not exist.");
        }
        audience.setCode(audienceId);
        return repository.save(audience);
    }

    public void deleteAudience(String audienceId) {
        repository.deleteById(audienceId);
    }

    public List<AudienceDto> getAllAudiencesTranslated(String language) {
        List<Audience> audiences = getAllAudiences();
        return audiences.stream().map(audience -> {
            String translatedName;
            
            // Simple translation mapping - in a full app, this would be stored in the database
            if ("tr".equals(language)) {
                switch (audience.getCode()) {
                    case "general":
                        translatedName = "Genel Halk";
                        break;
                    case "government":
                        translatedName = "Devlet";
                        break;
                    case "religious":
                        translatedName = "Dini";
                        break;
                    case "educational":
                        translatedName = "Eğitim";
                        break;
                    case "military":
                        translatedName = "Askeri";
                        break;
                    case "banking":
                        translatedName = "Bankacılık";
                        break;
                    case "health":
                        translatedName = "Sağlık";
                        break;
                    case "private_sector":
                        translatedName = "Özel Sektör";
                        break;
                    case "students":
                        translatedName = "Öğrenciler";
                        break;
                    default:
                        translatedName = audience.getAudienceName();
                        break;
                }
            } else {
                translatedName = audience.getAudienceName();
            }
            
            return new AudienceDto(audience.getCode(), translatedName);
        }).collect(Collectors.toList());
    }
}