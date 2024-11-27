package com.learntrack.resourceserver.services;

import com.learntrack.resourceserver.models.Course;
import com.learntrack.resourceserver.repositories.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Optional<Course> findByIdAndOwnerId(Long id, Long ownerId) {
        return courseRepository.findByIdAndOwnerId(id, ownerId);
    }

    public long count() {
        return courseRepository.count();
    }
}
