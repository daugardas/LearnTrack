package com.learntrack.server.dto;

import jakarta.validation.constraints.NotBlank;

public class ReviewDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String content;

    public ReviewDTO() {
    }

    public ReviewDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
