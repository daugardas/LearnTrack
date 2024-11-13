package com.learntrack.resourceserver.repositories;

import org.springframework.data.repository.CrudRepository;

import com.learntrack.resourceserver.models.Review;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    Iterable<Review> findAllByLessonId(Long lessonId);
}
