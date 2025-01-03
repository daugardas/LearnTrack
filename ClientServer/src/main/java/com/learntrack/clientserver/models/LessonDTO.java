package com.learntrack.clientserver.models;

public class LessonDTO {
    private String title;
    private String description;

    public LessonDTO() {
    }

    public LessonDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
