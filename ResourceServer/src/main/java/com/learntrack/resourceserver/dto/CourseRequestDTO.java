package com.learntrack.resourceserver.dto;

import jakarta.validation.constraints.NotBlank;

public class CourseRequestDTO {
    @NotBlank(message = "Name is mandatory")
    private String name;
    private String description;

    public CourseRequestDTO() {
    }

    public CourseRequestDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
