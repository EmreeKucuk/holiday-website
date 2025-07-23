package com.emre.holidayapi.dto;

public class AudienceDto {
    public String code;
    public String name;
    
    public AudienceDto() {}
    
    public AudienceDto(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
