package com.learntrack.resourceserver.repositories;

import org.springframework.data.repository.CrudRepository;

import com.learntrack.resourceserver.models.Course;

public interface CourseRepository extends CrudRepository<Course, Long> {

}
