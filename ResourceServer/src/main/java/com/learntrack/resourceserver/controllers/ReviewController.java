package com.learntrack.resourceserver.controllers;

import com.learntrack.resourceserver.annotations.CurrentUserIdResolver;
import com.learntrack.resourceserver.converters.ReviewConverter;
import com.learntrack.resourceserver.dto.ReviewRequestDTO;
import com.learntrack.resourceserver.dto.ReviewResponseDTO;
import com.learntrack.resourceserver.exceptions.ResourceNotFoundException;
import com.learntrack.resourceserver.models.Course;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/lessons/{lessonId}/reviews")
@Tag(name = "Review", description = "Endpoints for managing review information")
@Validated
public class ReviewController {
    private final ReviewService reviewService;
    private final LessonService lessonService;
    private final CourseService courseService;

    Logger logger = LoggerFactory.getLogger(ReviewController.class);

    public ReviewController(
            // @formatter:off
        ReviewService reviewService, 
        LessonService lessonService, 
        CourseService courseService
        // @formatter:on
    ) {
        this.reviewService = reviewService;
        this.lessonService = lessonService;
        this.courseService = courseService;
    }

    @Operation(summary = "Find all reviews for a lesson")
    // @formatter:off
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Successfully retrieved list of reviews", 
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                    array = @ArraySchema(
                        schema = @Schema(implementation = ReviewResponseDTO.class)
                    )
                )
            ),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    // @formatter:on
    @GetMapping()
    public ResponseEntity<Iterable<ReviewResponseDTO>> findAll(
            // @formatter:off
        @PathVariable Long courseId,
        @PathVariable Long lessonId,
        @CurrentSecurityContext(expression = "authentication") Authentication authentication
        // @formatter:on
    ) {
        Long userId = CurrentUserIdResolver.getCurrentUserId(authentication);
        if (userId == null) {
            logger.info("Anonymous user is trying to find all reviews for lesson with id: " + lessonId);
        } else {
            logger.info("Principal '{}' is trying to find all reviews for lesson with id: " + lessonId, userId);
        }

        logger.debug("Finding all reviews for lesson with id: " + lessonId);

        if (!courseService.existsById(courseId)) {
            logger.error("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        if (!lessonService.existsById(lessonId)) {
            logger.error("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found");
        }

        Iterable<ReviewResponseDTO> reviews = ReviewConverter.convertToReviewResponseDTOList(reviewService.findAllByLessonId(lessonId));
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Find review by id")
    // @formatter:off
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Successfully retrieved review", 
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                    schema = @Schema(implementation = ReviewResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    // @formatter:on
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> findById(
            // @formatter:off
        @PathVariable Long courseId,
        @PathVariable Long lessonId,
        @PathVariable Long reviewId,
        @CurrentSecurityContext(expression = "authentication") Authentication authentication
        // @formatter:on
    ) {
        Long userId = CurrentUserIdResolver.getCurrentUserId(authentication);
        if (userId == null) {
            logger.info("Anonymous user is trying to find review with id: " + reviewId + " for lesson with id: " + lessonId);
        } else {
            logger.info("Principal '{}' is trying to find review with id: " + reviewId + " for lesson with id: " + lessonId, userId);
        }

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

            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
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
            throw new ResourceNotFoundException("Review with id: " + reviewId + " not found for lesson with id: " + lessonId);
        }

        ReviewResponseDTO reviewResponseDTO = ReviewConverter.convertToReviewResponseDTO(review);

        return ResponseEntity.ok(reviewResponseDTO);
    }

    @Operation(summary = "Create a new review for a lesson")
    // @formatter:off
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "201", 
                description = "Successfully created review", 
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                    schema = @Schema(implementation = ReviewResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    // @formatter:on
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> create(
            // @formatter:off
        @PathVariable Long courseId, 
        @PathVariable Long lessonId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, 
            description = "Review to create", 
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = ReviewRequestDTO.class)
            )
        ) @Valid @RequestBody ReviewRequestDTO review,
        UriComponentsBuilder uriBuilder,
        @CurrentSecurityContext(expression = "authentication") Authentication authentication
        // @formatter:on
    ) {
        Long userId = CurrentUserIdResolver.getCurrentUserId(authentication);
        if (userId == null) {
            logger.info("Anonymous user is trying to create a new review for lesson with id: " + lessonId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            logger.info("Principal '{}' is trying to create a new review for lesson with id: " + lessonId, userId);
            // Jwt jwt = (Jwt) authentication.getPrincipal();
            // @SuppressWarnings("unchecked") List<String> roles = (List<String>) jwt.getClaims().get("roles");
            // if (!roles.contains("ROLE_LECTURER") && !roles.contains("ROLE_ADMIN")) {
            //     logger.info("Principal '{}' is not an admin or lecturer", userId);
            //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            // }
        }

        logger.info("Creating review for lesson with id: " + lessonId);

        Optional<Course> courseOptional = courseService.findById(courseId);
        if (!courseOptional.isPresent()) {
            logger.info("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Course course = courseOptional.get();

        // if (!course.getOwnerId().equals(userId)) {
        //     logger.info("Principal '{}' is not the owner of the course '{}'", userId, courseId);
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        // }

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.info("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found");
        }

        Lesson lesson = lessonOptional.get();

        if (!lesson.getCourse().getId().equals(courseId)) {
            logger.error("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        Review newReview = new Review(review, lesson, userId);

        Review savedReview = reviewService.save(newReview);

        ReviewResponseDTO reviewResponseDTO = ReviewConverter.convertToReviewResponseDTO(savedReview);

        URI location = uriBuilder.path("/api/v1/courses/{courseId}/lessons/{lessonId}/reviews/{reviewId}").buildAndExpand(courseId, lessonId, savedReview.getId()).toUri();
        return ResponseEntity.created(location).body(reviewResponseDTO);
    }

    @Operation(summary = "Update a review by id")
    // @formatter:off
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Review successfully updated", 
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                    schema = @Schema(implementation = ReviewResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    // @formatter:on
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> update(
            // @formatter:off
        @PathVariable Long courseId, 
        @PathVariable Long lessonId,
        @PathVariable Long reviewId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, 
            description = "Review to update", 
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = ReviewRequestDTO.class)
            )
        ) @Valid @RequestBody ReviewRequestDTO review,
        @CurrentSecurityContext(expression = "authentication") Authentication authentication
        // @formatter:on
    ) {
        Long userId = CurrentUserIdResolver.getCurrentUserId(authentication);
        if (userId == null) {
            logger.info("Anonymous user is trying to update review with id: " + reviewId + " for lesson with id: " + lessonId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            logger.info("Principal '{}' is trying to update review with id: " + reviewId + " for lesson with id: " + lessonId, userId);
        }

        logger.debug("Updating review with id: " + reviewId + " for lesson with id: " + lessonId);

        Optional<Course> courseOptional = courseService.findById(courseId);
        if (!courseOptional.isPresent()) {
            logger.error("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Course course = courseOptional.get();

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.error("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found");
        }

        Lesson lesson = lessonOptional.get();

        if (lesson.getCourse().getId() != courseId) {
            logger.error("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        Optional<Review> reviewOptional = reviewService.findById(reviewId);
        if (!reviewOptional.isPresent()) {
            logger.error("Review with id: " + reviewId + " not found");
            throw new ResourceNotFoundException("Review with id: " + reviewId + " not found");
        }

        Review reviewToUpdate = reviewOptional.get();
        if (reviewToUpdate.getLesson().getId() != lessonId) {
            logger.error("Review with id: " + reviewId + " not found for lesson with id: " + lessonId);
            throw new ResourceNotFoundException("Review with id: " + reviewId + " not found for lesson with id: " + lessonId);
        }

        // check if the user is the creator of the review
        if (!reviewToUpdate.getOwnerId().equals(userId)) {
            logger.info("Principal '{}' is not the creator of the review '{}'", userId, reviewId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }   

        if (review.getTitle() != null) reviewToUpdate.setTitle(review.getTitle());

        if (review.getContent() != null) reviewToUpdate.setContent(review.getContent());
        Review updatedReview = reviewService.save(reviewToUpdate);

        ReviewResponseDTO reviewResponseDTO = ReviewConverter.convertToReviewResponseDTO(updatedReview);

        return ResponseEntity.ok(reviewResponseDTO);
    }

    @Operation(summary = "Delete a review by id")
    // @formatter:off
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "204", 
                description = "Review successfully deleted"
            ),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    // @formatter:on
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(
            // @formatter:off
        @PathVariable Long courseId, 
        @PathVariable Long lessonId,
        @PathVariable Long reviewId,
        @CurrentSecurityContext(expression = "authentication") Authentication authentication
        // @formatter:on
    ) {
        Long userId = CurrentUserIdResolver.getCurrentUserId(authentication);
        if (userId == null) {
            logger.info("Anonymous user is trying to delete review with id: " + reviewId + " for lesson with id: " + lessonId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            logger.info("Principal '{}' is trying to delete review with id: " + reviewId + " for lesson with id: " + lessonId, userId);
        }
        logger.debug("Deleting review with id: " + reviewId);

        Optional<Course> courseOptional = courseService.findById(courseId);
        if (!courseOptional.isPresent()) {
            logger.error("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Course course = courseOptional.get();


        if (!lessonService.existsById(lessonId)) {
            logger.error("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found");
        }

        Optional<Review> reviewOptional = reviewService.findById(reviewId);
        if (!reviewOptional.isPresent()) {
            logger.error("Review with id: " + reviewId + " not found");
            throw new ResourceNotFoundException("Review with id: " + reviewId + " not found");
        }

        // check if the user is the creator of the review
        if (!reviewOptional.get().getOwnerId().equals(userId)) {
            logger.info("Principal '{}' is not the creator of the review '{}'", userId, reviewId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }   

        reviewService.deleteById(reviewId);
        return ResponseEntity.noContent().build();
    }
}
