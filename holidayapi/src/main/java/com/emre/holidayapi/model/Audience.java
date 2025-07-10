package com.emre.holidayapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "audiences")
public class Audience {
    @Id
    @Column(length = 50)
    private String code;

    @Column(name = "audience_name", length = 255)
    private String audienceName;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAudienceName() {
        return audienceName;
    }

    public void setAudienceName(String audienceName) {
        this.audienceName = audienceName;
    }
}