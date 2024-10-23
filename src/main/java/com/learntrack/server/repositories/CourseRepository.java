package com.learntrack.server.repositories;

import org.springframework.data.repository.CrudRepository;

import com.learntrack.server.models.Course;

public interface CourseRepository extends CrudRepository<Course, Long> {

}
