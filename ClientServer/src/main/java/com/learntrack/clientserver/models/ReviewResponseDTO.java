package com.learntrack.clientserver.models;

public class ReviewResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Long lessonId;
    private Long creatorId;

    public ReviewResponseDTO() {
    }

    public ReviewResponseDTO(Long id, String title, String content, Long lessonId, Long creatorId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.lessonId = lessonId;
        this.creatorId = creatorId;
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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
}
