package com.learntrack.resourceserver.repositories;

import com.learntrack.resourceserver.models.Lesson;
import org.springframework.data.repository.CrudRepository;

public interface LessonRepository extends CrudRepository<Lesson, Long> {
    Iterable<Lesson> findAllByCourseId(Long courseId);
}
