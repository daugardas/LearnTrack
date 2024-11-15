package com.learntrack.resourceserver.repositories;

import com.learntrack.resourceserver.models.Course;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Long> {
    Optional<Course> findByIdAndOwnerId(Long id, Long ownerId);
}
