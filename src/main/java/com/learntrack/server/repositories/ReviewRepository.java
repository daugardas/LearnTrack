package com.learntrack.server.repositories;

import com.learntrack.server.models.Review;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    Iterable<Review> findAllByLessonId(Long lessonId);
}
