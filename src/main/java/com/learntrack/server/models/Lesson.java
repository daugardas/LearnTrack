package com.learntrack.server.models;

import java.util.List;

import com.learntrack.server.dto.LessonRequestDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotBlank
    private String title;
    @NotBlank
    private String description;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    public Lesson() {
    }

    public Lesson(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Lesson(String title, String description, Course course) {
        this.title = title;
        this.description = description;
        this.course = course;
    }

    public Lesson(LessonRequestDTO lessonDTO) {
        this.title = lessonDTO.getTitle();
        this.description = lessonDTO.getDescription();
    }

    public Lesson(LessonRequestDTO lessonDTO, Course course) {
        this.title = lessonDTO.getTitle();
        this.description = lessonDTO.getDescription();
        this.course = course;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}