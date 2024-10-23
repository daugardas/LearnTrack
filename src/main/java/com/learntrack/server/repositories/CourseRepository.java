package com.learntrack.server.repositories;

import com.learntrack.server.models.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Long> {

}
