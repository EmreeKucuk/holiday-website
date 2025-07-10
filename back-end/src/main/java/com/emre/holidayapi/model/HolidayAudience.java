package main.java.com.emre.holidayapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "holiday_audience")
public class HolidayAudience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // holiday_definition ile ilişki (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_definition_id", nullable = false)
    private HolidayDefinition holidayDefinition;

    @Column(name = "audience", nullable = false)
    private String audience;  // Örneğin: "blue_collar", "white_collar", "all"

    // Constructors, getters, setters

    public HolidayAudience() {
    }

    public HolidayAudience(HolidayDefinition holidayDefinition, String audience) {
        this.holidayDefinition = holidayDefinition;
        this.audience = audience;
    }

    public Long getId() {
        return id;
    }

    public HolidayDefinition getHolidayDefinition() {
        return holidayDefinition;
    }

    public void setHolidayDefinition(HolidayDefinition holidayDefinition) {
        this.holidayDefinition = holidayDefinition;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }
}
