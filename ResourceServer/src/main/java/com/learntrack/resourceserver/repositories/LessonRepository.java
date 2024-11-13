package com.learntrack.resourceserver.repositories;

import org.springframework.data.repository.CrudRepository;

import com.learntrack.resourceserver.models.Lesson;

public interface LessonRepository extends CrudRepository<Lesson, Long> {
    Iterable<Lesson> findAllByCourseId(Long courseId);
}
