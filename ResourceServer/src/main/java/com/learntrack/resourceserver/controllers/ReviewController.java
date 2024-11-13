package com.learntrack.resourceserver.controllers;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.learntrack.resourceserver.converters.ReviewConverter;
import com.learntrack.resourceserver.dto.ReviewRequestDTO;
import com.learntrack.resourceserver.dto.ReviewResponseDTO;
import com.learntrack.resourceserver.exceptions.ResourceNotFoundException;
import com.learntrack.resourceserver.models.Lesson;
import com.learntrack.resourceserver.models.Review;
import com.learntrack.resourceserver.services.CourseService;
import com.learntrack.resourceserver.services.LessonService;
import com.learntrack.resourceserver.services.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/lessons/{lessonId}/reviews")
@Tag(name = "Review", description = "Endpoints for managing review information")
@Validated
public class ReviewController {
    private final ReviewService reviewService;
    private final LessonService lessonService;
    private final CourseService courseService;

    Logger logger = LoggerFactory.getLogger(ReviewController.class);

    public ReviewController(ReviewService reviewService, LessonService lessonService, CourseService courseService) {
        this.reviewService = reviewService;
        this.lessonService = lessonService;
        this.courseService = courseService;
    }

    @Operation(summary = "Find all reviews for a lesson")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of reviews", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ReviewResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<Iterable<ReviewResponseDTO>> findAll(@PathVariable Long courseId,
            @PathVariable Long lessonId) {
        logger.debug("Finding all reviews for lesson with id: " + lessonId);

        if (!courseService.existsById(courseId)) {
            logger.error("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        if (!lessonService.existsById(lessonId)) {
            logger.error("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found");
        }

        Iterable<ReviewResponseDTO> reviews = ReviewConverter.convertToReviewResponseDTOList(
                reviewService.findAllByLessonId(lessonId));
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Find review by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved review", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReviewResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> findById(@PathVariable Long courseId, @PathVariable Long lessonId,
            @PathVariable Long reviewId) {

        logger.info("Finding course with id: " + courseId);

        if (!courseService.existsById(courseId)) {
            logger.error("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        logger.info("Finding lesson with id: " + lessonId);

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.error("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found");
        }

        if (!lessonOptional.get().getCourse().getId().equals(courseId)) {
            logger.info("Lesson with id: " + lessonId + " not found for course with id: " + courseId);

            throw new ResourceNotFoundException(
                    "Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        Optional<Review> reviewOptional = reviewService.findById(reviewId);
        if (!reviewOptional.isPresent()) {
            logger.error("Review with id: " + reviewId + " not found");
            throw new ResourceNotFoundException("Review with id: " + reviewId + " not found");
        }

        Review review = reviewOptional.get();

        logger.info("Review found: " + review.getId());
        logger.info("Review lesson id: " + review.getLesson().getId());

        if (!review.getLesson().getId().equals(lessonId)) {
            logger.error("Review with id: " + reviewId + " not found for lesson with id: " + lessonId);
            throw new ResourceNotFoundException(
                    "Review with id: " + reviewId + " not found for lesson with id: " + lessonId);
        }

        ReviewResponseDTO reviewResponseDTO = ReviewConverter.convertToReviewResponseDTO(review);

        return ResponseEntity.ok(reviewResponseDTO);
    }

    @Operation(summary = "Create a new review for a lesson")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created review", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReviewResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> create(@PathVariable Long courseId, @PathVariable Long lessonId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Review to create", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReviewRequestDTO.class))) @Valid @RequestBody ReviewRequestDTO review,
            UriComponentsBuilder uriBuilder) {
        logger.info("Creating review for lesson with id: " + lessonId);

        if (!courseService.existsById(courseId)) {
            logger.info("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.info("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found");
        }

        System.out.println("lesson optional id: " + lessonOptional.get().getId() + " lesson course id: "
                + lessonOptional.get().getCourse().getId());

        if (!lessonOptional.get().getCourse().getId().equals(courseId)) {
            logger.error("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
            throw new ResourceNotFoundException(
                    "Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        Review newReview = new Review(review);

        newReview.setLesson(lessonOptional.get());
        Review savedReview = reviewService.save(newReview);

        ReviewResponseDTO reviewResponseDTO = ReviewConverter.convertToReviewResponseDTO(savedReview);

        URI location = uriBuilder.path("/api/v1/courses/{courseId}/lessons/{lessonId}/reviews/{reviewId}")
                .buildAndExpand(courseId, lessonId, savedReview.getId()).toUri();
        return ResponseEntity.created(location).body(reviewResponseDTO);
    }

    @Operation(summary = "Update a review by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review successfully updated", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReviewResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> update(@PathVariable Long courseId, @PathVariable Long lessonId,
            @PathVariable Long reviewId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Review to update", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReviewRequestDTO.class))) @Valid @RequestBody ReviewRequestDTO review) {
        logger.debug("Updating review with id: " + reviewId + " for lesson with id: " + lessonId);

        if (!courseService.existsById(courseId)) {
            logger.error("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.error("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found");
        }

        Lesson lesson = lessonOptional.get();

        if (lesson.getCourse().getId() != courseId) {
            logger.error("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
            throw new ResourceNotFoundException(
                    "Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        Optional<Review> reviewOptional = reviewService.findById(reviewId);
        if (!reviewOptional.isPresent()) {
            logger.error("Review with id: " + reviewId + " not found");
            throw new ResourceNotFoundException("Review with id: " + reviewId + " not found");
        }

        Review reviewToUpdate = reviewOptional.get();
        if (reviewToUpdate.getLesson().getId() != lessonId) {
            logger.error("Review with id: " + reviewId + " not found for lesson with id: " + lessonId);
            throw new ResourceNotFoundException(
                    "Review with id: " + reviewId + " not found for lesson with id: " + lessonId);
        }

        if (review.getTitle() != null)
            reviewToUpdate.setTitle(review.getTitle());

        if (review.getContent() != null)
            reviewToUpdate.setContent(review.getContent());
        Review updatedReview = reviewService.save(reviewToUpdate);

        ReviewResponseDTO reviewResponseDTO = ReviewConverter.convertToReviewResponseDTO(updatedReview);

        return ResponseEntity.ok(reviewResponseDTO);
    }

    @Operation(summary = "Delete a review by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable Long courseId, @PathVariable Long lessonId,
            @PathVariable Long reviewId) {
        logger.debug("Deleting review with id: " + reviewId);

        if (!courseService.existsById(courseId)) {
            logger.error("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        if (!lessonService.existsById(lessonId)) {
            logger.error("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found");
        }

        if (!reviewService.existsById(reviewId)) {
            logger.error("Review with id: " + reviewId + " not found");
            throw new ResourceNotFoundException("Review with id: " + reviewId + " not found");
        }

        reviewService.deleteById(reviewId);
        return ResponseEntity.noContent().build();
    }
}
