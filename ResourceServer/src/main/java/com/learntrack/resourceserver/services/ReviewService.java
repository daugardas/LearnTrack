package com.learntrack.resourceserver.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.learntrack.resourceserver.models.Review;
import com.learntrack.resourceserver.repositories.ReviewRepository;

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
