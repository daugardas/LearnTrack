package com.learntrack.server.models;

import com.learntrack.server.dto.ReviewRequestDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotBlank
    private String title;
    @NotBlank
    private String content;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    public Review() {
    }

    public Review(String title, String content) {
        this.title = title;
    }

    public Review(String title, String content, Lesson lesson) {
        this.title = title;
        this.content = content;
        this.lesson = lesson;
    }

    public Review(ReviewRequestDTO reviewDTO) {
        this.title = reviewDTO.getTitle();
        this.content = reviewDTO.getContent();
    }

    public Review(ReviewRequestDTO reviewDTO, Lesson lesson) {
        this.title = reviewDTO.getTitle();
        this.content = reviewDTO.getContent();
        this.lesson = lesson;
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

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }
}