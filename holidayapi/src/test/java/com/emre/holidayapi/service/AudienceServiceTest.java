package com.emre.holidayapi.service;

import com.emre.holidayapi.dto.AudienceDto;
import com.emre.holidayapi.model.Audience;
import com.emre.holidayapi.repository.AudienceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AudienceServiceTest {

    @Mock
    private AudienceRepository repository;

    @InjectMocks
    private AudienceService audienceService;

    private Audience generalAudience;
    private Audience governmentAudience;
    private Audience religiousAudience;

    @BeforeEach
    void setUp() {
        generalAudience = new Audience();
        generalAudience.setCode("general");
        generalAudience.setAudienceName("General Public");

        governmentAudience = new Audience();
        governmentAudience.setCode("government");
        governmentAudience.setAudienceName("Government");

        religiousAudience = new Audience();
        religiousAudience.setCode("religious");
        religiousAudience.setAudienceName("Religious");
    }

    @Test
    void getAllAudiences_ShouldReturnAllAudiences() {
        // Given
        List<Audience> expectedAudiences = Arrays.asList(generalAudience, governmentAudience, religiousAudience);
        when(repository.findAll()).thenReturn(expectedAudiences);

        // When
        List<Audience> actualAudiences = audienceService.getAllAudiences();

        // Then
        assertThat(actualAudiences)
                .hasSize(3)
                .containsExactly(generalAudience, governmentAudience, religiousAudience);
        verify(repository).findAll();
    }

    @Test
    void addAudience_WhenAudienceDoesNotExist_ShouldSaveAndReturnAudience() {
        // Given
        when(repository.existsById("new_audience")).thenReturn(false);
        when(repository.save(any(Audience.class))).thenReturn(generalAudience);

        Audience newAudience = new Audience();
        newAudience.setCode("new_audience");
        newAudience.setAudienceName("New Audience");

        // When
        Audience savedAudience = audienceService.addAudience(newAudience);

        // Then
        assertThat(savedAudience).isEqualTo(generalAudience);
        verify(repository).existsById("new_audience");
        verify(repository).save(newAudience);
    }

    @Test
    void addAudience_WhenAudienceAlreadyExists_ShouldThrowException() {
        // Given
        when(repository.existsById("general")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> audienceService.addAudience(generalAudience))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Audience with code general already exists.");

        verify(repository).existsById("general");
        verify(repository, never()).save(any(Audience.class));
    }

    @Test
    void updateAudience_WhenAudienceExists_ShouldUpdateAndReturnAudience() {
        // Given
        String audienceId = "general";
        when(repository.findById(audienceId)).thenReturn(Optional.of(generalAudience));
        when(repository.save(any(Audience.class))).thenReturn(generalAudience);

        Audience updatedAudience = new Audience();
        updatedAudience.setAudienceName("Updated General Public");

        // When
        Audience result = audienceService.updateAudience(audienceId, updatedAudience);

        // Then
        assertThat(result).isEqualTo(generalAudience);
        assertThat(updatedAudience.getCode()).isEqualTo(audienceId);
        verify(repository).findById(audienceId);
        verify(repository).save(updatedAudience);
    }

    @Test
    void updateAudience_WhenAudienceDoesNotExist_ShouldThrowException() {
        // Given
        String audienceId = "nonexistent";
        when(repository.findById(audienceId)).thenReturn(Optional.empty());

        Audience updatedAudience = new Audience();
        updatedAudience.setAudienceName("Updated Name");

        // When & Then
        assertThatThrownBy(() -> audienceService.updateAudience(audienceId, updatedAudience))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Audience with id nonexistent does not exist.");

        verify(repository).findById(audienceId);
        verify(repository, never()).save(any(Audience.class));
    }

    @Test
    void deleteAudience_ShouldCallRepositoryDeleteById() {
        // Given
        String audienceId = "general";

        // When
        audienceService.deleteAudience(audienceId);

        // Then
        verify(repository).deleteById(audienceId);
    }

    @Test
    void getAllAudiencesTranslated_WithEnglishLanguage_ShouldReturnOriginalNames() {
        // Given
        List<Audience> audiences = Arrays.asList(generalAudience, governmentAudience, religiousAudience);
        when(repository.findAll()).thenReturn(audiences);

        // When
        List<AudienceDto> translatedAudiences = audienceService.getAllAudiencesTranslated("en");

        // Then
        assertThat(translatedAudiences).hasSize(3);
        assertThat(translatedAudiences.get(0).getCode()).isEqualTo("general");
        assertThat(translatedAudiences.get(0).getName()).isEqualTo("General Public");
        assertThat(translatedAudiences.get(1).getCode()).isEqualTo("government");
        assertThat(translatedAudiences.get(1).getName()).isEqualTo("Government");
        assertThat(translatedAudiences.get(2).getCode()).isEqualTo("religious");
        assertThat(translatedAudiences.get(2).getName()).isEqualTo("Religious");
        verify(repository).findAll();
    }

    @Test
    void getAllAudiencesTranslated_WithTurkishLanguage_ShouldReturnTranslatedNames() {
        // Given
        List<Audience> audiences = Arrays.asList(generalAudience, governmentAudience, religiousAudience);
        when(repository.findAll()).thenReturn(audiences);

        // When
        List<AudienceDto> translatedAudiences = audienceService.getAllAudiencesTranslated("tr");

        // Then
        assertThat(translatedAudiences).hasSize(3);
        assertThat(translatedAudiences.get(0).getCode()).isEqualTo("general");
        assertThat(translatedAudiences.get(0).getName()).isEqualTo("Genel Halk");
        assertThat(translatedAudiences.get(1).getCode()).isEqualTo("government");
        assertThat(translatedAudiences.get(1).getName()).isEqualTo("Devlet");
        assertThat(translatedAudiences.get(2).getCode()).isEqualTo("religious");
        assertThat(translatedAudiences.get(2).getName()).isEqualTo("Dini");
        verify(repository).findAll();
    }

    @Test
    void getAllAudiencesTranslated_WithTurkishLanguage_ForEducationalAudience_ShouldReturnTranslatedName() {
        // Given
        Audience educationalAudience = new Audience();
        educationalAudience.setCode("educational");
        educationalAudience.setAudienceName("Educational");

        List<Audience> audiences = Arrays.asList(educationalAudience);
        when(repository.findAll()).thenReturn(audiences);

        // When
        List<AudienceDto> translatedAudiences = audienceService.getAllAudiencesTranslated("tr");

        // Then
        assertThat(translatedAudiences).hasSize(1);
        assertThat(translatedAudiences.get(0).getCode()).isEqualTo("educational");
        assertThat(translatedAudiences.get(0).getName()).isEqualTo("Eğitim");
        verify(repository).findAll();
    }

    @Test
    void getAllAudiencesTranslated_WithTurkishLanguage_ForMilitary_ShouldReturnTranslatedName() {
        // Given
        Audience militaryAudience = new Audience();
        militaryAudience.setCode("military");
        militaryAudience.setAudienceName("Military");

        List<Audience> audiences = Arrays.asList(militaryAudience);
        when(repository.findAll()).thenReturn(audiences);

        // When
        List<AudienceDto> translatedAudiences = audienceService.getAllAudiencesTranslated("tr");

        // Then
        assertThat(translatedAudiences).hasSize(1);
        assertThat(translatedAudiences.get(0).getCode()).isEqualTo("military");
        assertThat(translatedAudiences.get(0).getName()).isEqualTo("Askeri");
        verify(repository).findAll();
    }

    @Test
    void getAllAudiencesTranslated_WithTurkishLanguage_ForBanking_ShouldReturnTranslatedName() {
        // Given
        Audience bankingAudience = new Audience();
        bankingAudience.setCode("banking");
        bankingAudience.setAudienceName("Banking");

        List<Audience> audiences = Arrays.asList(bankingAudience);
        when(repository.findAll()).thenReturn(audiences);

        // When
        List<AudienceDto> translatedAudiences = audienceService.getAllAudiencesTranslated("tr");

        // Then
        assertThat(translatedAudiences).hasSize(1);
        assertThat(translatedAudiences.get(0).getCode()).isEqualTo("banking");
        assertThat(translatedAudiences.get(0).getName()).isEqualTo("Bankacılık");
        verify(repository).findAll();
    }

    @Test
    void getAllAudiencesTranslated_WithTurkishLanguage_ForHealth_ShouldReturnTranslatedName() {
        // Given
        Audience healthAudience = new Audience();
        healthAudience.setCode("health");
        healthAudience.setAudienceName("Health");

        List<Audience> audiences = Arrays.asList(healthAudience);
        when(repository.findAll()).thenReturn(audiences);

        // When
        List<AudienceDto> translatedAudiences = audienceService.getAllAudiencesTranslated("tr");

        // Then
        assertThat(translatedAudiences).hasSize(1);
        assertThat(translatedAudiences.get(0).getCode()).isEqualTo("health");
        assertThat(translatedAudiences.get(0).getName()).isEqualTo("Sağlık");
        verify(repository).findAll();
    }

    @Test
    void getAllAudiencesTranslated_WithTurkishLanguage_ForPrivateSector_ShouldReturnTranslatedName() {
        // Given
        Audience privateSectorAudience = new Audience();
        privateSectorAudience.setCode("private_sector");
        privateSectorAudience.setAudienceName("Private Sector");

        List<Audience> audiences = Arrays.asList(privateSectorAudience);
        when(repository.findAll()).thenReturn(audiences);

        // When
        List<AudienceDto> translatedAudiences = audienceService.getAllAudiencesTranslated("tr");

        // Then
        assertThat(translatedAudiences).hasSize(1);
        assertThat(translatedAudiences.get(0).getCode()).isEqualTo("private_sector");
        assertThat(translatedAudiences.get(0).getName()).isEqualTo("Özel Sektör");
        verify(repository).findAll();
    }

    @Test
    void getAllAudiencesTranslated_WithTurkishLanguage_ForStudents_ShouldReturnTranslatedName() {
        // Given
        Audience studentsAudience = new Audience();
        studentsAudience.setCode("students");
        studentsAudience.setAudienceName("Students");

        List<Audience> audiences = Arrays.asList(studentsAudience);
        when(repository.findAll()).thenReturn(audiences);

        // When
        List<AudienceDto> translatedAudiences = audienceService.getAllAudiencesTranslated("tr");

        // Then
        assertThat(translatedAudiences).hasSize(1);
        assertThat(translatedAudiences.get(0).getCode()).isEqualTo("students");
        assertThat(translatedAudiences.get(0).getName()).isEqualTo("Öğrenciler");
        verify(repository).findAll();
    }

    @Test
    void getAllAudiencesTranslated_WithTurkishLanguage_ForUnknownCode_ShouldReturnOriginalName() {
        // Given
        Audience unknownAudience = new Audience();
        unknownAudience.setCode("unknown_code");
        unknownAudience.setAudienceName("Unknown Audience");

        List<Audience> audiences = Arrays.asList(unknownAudience);
        when(repository.findAll()).thenReturn(audiences);

        // When
        List<AudienceDto> translatedAudiences = audienceService.getAllAudiencesTranslated("tr");

        // Then
        assertThat(translatedAudiences).hasSize(1);
        assertThat(translatedAudiences.get(0).getCode()).isEqualTo("unknown_code");
        assertThat(translatedAudiences.get(0).getName()).isEqualTo("Unknown Audience");
        verify(repository).findAll();
    }

    @Test
    void getAllAudiencesTranslated_WhenNoAudiences_ShouldReturnEmptyList() {
        // Given
        when(repository.findAll()).thenReturn(Arrays.asList());

        // When
        List<AudienceDto> translatedAudiences = audienceService.getAllAudiencesTranslated("en");

        // Then
        assertThat(translatedAudiences).isEmpty();
        verify(repository).findAll();
    }
}
