package com.learntrack.server.dto;

public class ReviewResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Long lessonId;

    public ReviewResponseDTO() {
    }

    public ReviewResponseDTO(Long id, String title, String content, Long lessonId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.lessonId = lessonId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }
}
