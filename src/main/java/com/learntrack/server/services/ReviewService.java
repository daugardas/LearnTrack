package com.learntrack.server.services;

import com.learntrack.server.models.Review;
import com.learntrack.server.repositories.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Iterable<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public Iterable<Review> findAllByLessonId(Long lessonId) {
        return reviewRepository.findAllByLessonId(lessonId);
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return reviewRepository.existsById(id);
    }
}
