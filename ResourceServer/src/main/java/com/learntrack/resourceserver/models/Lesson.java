package com.learntrack.resourceserver.models;

import com.learntrack.resourceserver.dto.LessonRequestDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

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

    public Lesson(String title, String description, Long ownerId) {
        this.title = title;
        this.description = description;
        this.ownerId = ownerId;
    }

    public Lesson(String title, String description, Course course, Long ownerId) {
        this.title = title;
        this.description = description;
        this.course = course;
        this.ownerId = ownerId;
    }

    public Lesson(LessonRequestDTO lessonDTO, Long ownerId) {
        this.title = lessonDTO.getTitle();
        this.description = lessonDTO.getDescription();
        this.ownerId = ownerId;
    }

    public Lesson(LessonRequestDTO lessonDTO, Course course, Long ownerId) {
        this.title = lessonDTO.getTitle();
        this.description = lessonDTO.getDescription();
        this.course = course;
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