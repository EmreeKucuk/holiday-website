package com.emre.holidayapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "translations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"template_id", "language_code"})
})
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private HolidayTemplate template;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    @Column(name = "translated_name", nullable = false, length = 200)
    private String translatedName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HolidayTemplate getTemplate() {
        return template;
    }

    public void setTemplate(HolidayTemplate template) {
        this.template = template;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getTranslatedName() {
        return translatedName;
    }

    public void setTranslatedName(String translatedName) {
        this.translatedName = translatedName;
    }
}