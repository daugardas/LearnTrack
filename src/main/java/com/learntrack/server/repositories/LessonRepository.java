package com.learntrack.server.repositories;

import com.learntrack.server.models.Lesson;
import org.springframework.data.repository.CrudRepository;

public interface LessonRepository extends CrudRepository<Lesson, Long> {
    Iterable<Lesson> findAllByCourseId(Long courseId);
}
