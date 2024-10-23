package com.learntrack.server.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.learntrack.server.models.Course;
import com.learntrack.server.repositories.CourseRepository;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Iterable<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return courseRepository.existsById(id);
    }
}
