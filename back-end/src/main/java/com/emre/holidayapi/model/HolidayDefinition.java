package com.emre.holidayapi.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "holiday_definitions")
public class HolidayDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // HolidayTemplate ile ManyToOne ilişki
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private HolidayTemplate template;

    @Column(name = "holiday_date", nullable = false)
    private LocalDate holidayDate;

    // Getter - Setter

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

    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(LocalDate holidayDate) {
        this.holidayDate = holidayDate;
    }
}
