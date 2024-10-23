package com.learntrack.server.repositories;

import org.springframework.data.repository.CrudRepository;

import com.learntrack.server.models.Lesson;

public interface LessonRepository extends CrudRepository<Lesson, Long> {
    Iterable<Lesson> findAllByCourseId(Long courseId);
}
