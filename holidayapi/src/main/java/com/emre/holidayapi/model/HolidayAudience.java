package com.emre.holidayapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "holiday_audiences", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"definition_id", "audience_code"})
})
public class HolidayAudience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "definition_id", nullable = false)
    private HolidayDefinition definition;

    @ManyToOne
    @JoinColumn(name = "audience_code", nullable = false)
    private Audience audience;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HolidayDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(HolidayDefinition definition) {
        this.definition = definition;
    }

    public Audience getAudience() {
        return audience;
    }

    public void setAudience(Audience audience) {
        this.audience = audience;
    }
}
