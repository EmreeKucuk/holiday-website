package com.emre.holidayapi.repository;

import com.emre.holidayapi.model.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {
    
    @Query("SELECT t FROM Translation t WHERE t.template.id = :templateId AND t.languageCode = :languageCode")
    Optional<Translation> findByTemplateIdAndLanguageCode(@Param("templateId") Long templateId, @Param("languageCode") String languageCode);
}
