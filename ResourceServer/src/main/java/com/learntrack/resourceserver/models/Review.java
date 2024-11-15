package com.learntrack.resourceserver.models;

import com.learntrack.resourceserver.dto.ReviewRequestDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @NotBlank
    private String title;
    @NotBlank
    private String content;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    public Review() {
    }

    public Review(String title, String content, Long ownerId) {
        this.title = title;
        this.content = content;
        this.ownerId = ownerId;
    }

    public Review(String title, String content, Lesson lesson, Long ownerId) {
        this.title = title;
        this.content = content;
        this.lesson = lesson;
        this.ownerId = ownerId;
    }

    public Review(ReviewRequestDTO reviewDTO, Long ownerId) {
        this.title = reviewDTO.getTitle();
        this.content = reviewDTO.getContent();
        this.ownerId = ownerId;
    }

    public Review(ReviewRequestDTO reviewDTO, Lesson lesson, Long ownerId) {
        this.title = reviewDTO.getTitle();
        this.content = reviewDTO.getContent();
        this.lesson = lesson;
        this.ownerId = ownerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }
}