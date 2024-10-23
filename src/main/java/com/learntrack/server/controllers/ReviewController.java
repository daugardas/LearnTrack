package com.learntrack.server.controllers;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.learntrack.server.dto.ReviewDTO;
import com.learntrack.server.models.Lesson;
import com.learntrack.server.models.Review;
import com.learntrack.server.services.CourseService;
import com.learntrack.server.services.LessonService;
import com.learntrack.server.services.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/lessons/{lessonId}/reviews")
@Tag(name = "Review", description = "Endpoints for managing review information")
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
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of reviews", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = Review.class)))),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<Iterable<Review>> findAll(@PathVariable Long courseId, @PathVariable Long lessonId) {
        logger.debug("Finding all reviews for lesson with id: " + lessonId);

        if (!courseService.existsById(courseId)) {
            logger.error("Course with id: " + courseId + " not found");
            return ResponseEntity.notFound().build();
        }

        if (!lessonService.existsById(lessonId)) {
            logger.error("Lesson with id: " + lessonId + " not found");
            return ResponseEntity.notFound().build();
        }

        Iterable<Review> reviews = reviewService.findAllByLessonId(lessonId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Find review by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved review", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Review.class))),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> findById(@PathVariable Long courseId, @PathVariable Long lessonId,
            @PathVariable Long reviewId) {
        logger.debug("Finding review with id: " + reviewId);

        if (!courseService.existsById(courseId)) {
            logger.error("Course with id: " + courseId + " not found");
            return ResponseEntity.notFound().build();
        }

        if (!lessonService.existsById(lessonId)) {
            logger.error("Lesson with id: " + lessonId + " not found");
            return ResponseEntity.notFound().build();
        }

        if (!reviewService.existsById(reviewId)) {
            logger.error("Review with id: " + reviewId + " not found");
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(reviewService.findById(reviewId).get());
    }

    @Operation(summary = "Create a new review for a lesson")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created review", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Review.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Review> create(@PathVariable Long courseId, @PathVariable Long lessonId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Review to create", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReviewDTO.class))) @RequestBody ReviewDTO review,
            UriComponentsBuilder uriBuilder) {
        logger.debug("Creating review for lesson with id: " + lessonId);

        if (!courseService.existsById(courseId)) {
            logger.error("Course with id: " + courseId + " not found");
            return ResponseEntity.notFound().build();
        }

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.error("Lesson with id: " + lessonId + " not found");
            return ResponseEntity.notFound().build();
        }

        Review newReview = new Review(review);

        newReview.setLesson(lessonOptional.get());
        Review savedReview = reviewService.save(newReview);

        URI location = uriBuilder.path("/api/v1/courses/{courseId}/lessons/{lessonId}/reviews/{reviewId}")
                .buildAndExpand(courseId, lessonId, savedReview.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Update a review by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review successfully updated", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Review.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> update(@PathVariable Long courseId, @PathVariable Long lessonId,
            @PathVariable Long reviewId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Review to update", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReviewDTO.class))) @RequestBody ReviewDTO review) {
        logger.debug("Updating review with id: " + reviewId + " for lesson with id: " + lessonId);

        if (!courseService.existsById(courseId)) {
            logger.error("Course with id: " + courseId + " not found");
            return ResponseEntity.notFound().build();
        }

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.error("Lesson with id: " + lessonId + " not found");
            return ResponseEntity.notFound().build();
        }

        Optional<Review> reviewOptional = reviewService.findById(reviewId);
        if (!reviewOptional.isPresent()) {
            logger.error("Review with id: " + reviewId + " not found");
            return ResponseEntity.notFound().build();
        }

        Lesson lesson = lessonOptional.get();
        Review reviewToUpdate = reviewOptional.get();

        reviewToUpdate.setId(reviewId);
        reviewToUpdate.setLesson(lesson);
        Review updatedReview = reviewService.save(reviewToUpdate);
        return ResponseEntity.ok(updatedReview);
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
            return ResponseEntity.notFound().build();
        }

        if (!lessonService.existsById(lessonId)) {
            logger.error("Lesson with id: " + lessonId + " not found");
            return ResponseEntity.notFound().build();
        }

        if (!reviewService.existsById(reviewId)) {
            logger.error("Review with id: " + reviewId + " not found");
            return ResponseEntity.notFound().build();
        }

        reviewService.deleteById(reviewId);
        return ResponseEntity.noContent().build();
    }
}
