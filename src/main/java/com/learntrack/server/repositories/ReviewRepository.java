package com.learntrack.server.repositories;

import org.springframework.data.repository.CrudRepository;

import com.learntrack.server.models.Review;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    Iterable<Review> findAllByLessonId(Long lessonId);
}
