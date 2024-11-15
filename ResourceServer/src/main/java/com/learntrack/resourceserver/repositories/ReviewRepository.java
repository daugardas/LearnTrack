package com.learntrack.resourceserver.repositories;

import com.learntrack.resourceserver.models.Review;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    Iterable<Review> findAllByLessonId(Long lessonId);
}
