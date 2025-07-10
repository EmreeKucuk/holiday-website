package com.emre.holidayapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "country_holidays", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"country_code", "template_id"})
})
public class CountryHoliday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "country_code", nullable = false)
    private Country country;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private HolidayTemplate template;

    @Column(name = "is_active", length = 1)
    private String isActive = "Y";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public HolidayTemplate getTemplate() {
        return template;
    }

    public void setTemplate(HolidayTemplate template) {
        this.template = template;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}