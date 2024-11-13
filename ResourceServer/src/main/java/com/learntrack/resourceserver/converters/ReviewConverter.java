package com.learntrack.resourceserver.converters;

import java.util.ArrayList;
import java.util.List;

import com.learntrack.resourceserver.dto.ReviewRequestDTO;
import com.learntrack.resourceserver.dto.ReviewResponseDTO;
import com.learntrack.resourceserver.models.Review;

public class ReviewConverter {
    public static ReviewResponseDTO convertToReviewResponseDTO(Review review) {
        ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO();
        reviewResponseDTO.setId(review.getId());
        reviewResponseDTO.setTitle(review.getTitle());
        reviewResponseDTO.setContent(review.getContent());
        reviewResponseDTO.setLessonId(review.getLesson().getId());
        return reviewResponseDTO;
    }

    public static Review convertToEntity(ReviewRequestDTO reviewRequestDTO) {
        Review review = new Review();
        review.setTitle(reviewRequestDTO.getTitle());
        review.setContent(reviewRequestDTO.getContent());
        return review;
    }

    public static List<ReviewResponseDTO> convertToReviewResponseDTOList(Iterable<Review> reviews) {
        List<ReviewResponseDTO> reviewResponseDTOList = new ArrayList<>();
        for (Review review : reviews) {
            reviewResponseDTOList.add(convertToReviewResponseDTO(review));
        }
        return reviewResponseDTOList;
    }
}
