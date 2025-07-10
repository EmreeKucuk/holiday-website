package main.java.com.emre.holidayapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "audience")
public class Audience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "audience_code", nullable = false, unique = true)
    private String audienceCode;

    @Column(name = "description")
    private String description;

    // Constructors

    public Audience() {
    }

    public Audience(String audienceCode, String description) {
        this.audienceCode = audienceCode;
        this.description = description;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getAudienceCode() {
        return audienceCode;
    }

    public void setAudienceCode(String audienceCode) {
        this.audienceCode = audienceCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
