package com.learntrack.server.controllers;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.learntrack.server.dto.LessonDTO;
import com.learntrack.server.models.Course;
import com.learntrack.server.models.Lesson;
import com.learntrack.server.services.CourseService;
import com.learntrack.server.services.LessonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/lessons")
@Tag(name = "Lesson", description = "Endpoints for managing lesson information")
@Validated
public class LessonController {
    private final LessonService lessonService;
    private final CourseService courseService;
    Logger logger = LoggerFactory.getLogger(LessonController.class);

    public LessonController(LessonService lessonService, CourseService courseService) {
        this.lessonService = lessonService;
        this.courseService = courseService;
    }

    @Operation(summary = "Find all lessons for a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of lessons", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = Lesson.class)))),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)

    })
    @GetMapping()
    public ResponseEntity<Iterable<Lesson>> findAll(@PathVariable Long courseId) {
        logger.debug("Finding all lessons for course with id: " + courseId);

        if (!courseService.existsById(courseId)) {
            logger.error("Course with id: " + courseId + " not found");
            return ResponseEntity.notFound().build();
        }

        Iterable<Lesson> lessons = lessonService.findAllByCourseId(courseId);
        return ResponseEntity.ok(lessons);
    }

    @Operation(summary = "Find lesson by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved lesson", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Lesson.class))),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @GetMapping("/{lessonId}")
    public ResponseEntity<Lesson> findById(@PathVariable Long courseId, @PathVariable Long lessonId) {
        logger.debug("Finding lesson with id: " + lessonId + " for course with id: " + courseId);

        if (!courseService.existsById(courseId)) {
            logger.error("Course with id: " + courseId + " not found");
            return ResponseEntity.notFound().build();
        }

        return lessonService.findById(lessonId)
                .map(lesson -> ResponseEntity.ok(lesson))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new lesson for a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created lesson", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Lesson.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Lesson> create(@PathVariable Long courseId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Lesson to create", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LessonDTO.class))) @Valid @RequestBody LessonDTO lesson,
            UriComponentsBuilder uriBuilder) {
        logger.debug("Creating lesson for course with id: " + courseId);

        Optional<Course> courseOptional = courseService.findById(courseId);
        if (!courseOptional.isPresent()) {
            logger.error("Course with id: " + courseId + " not found");
            return ResponseEntity.notFound().build();
        }

        Lesson newLesson = new Lesson(lesson);

        newLesson.setCourse(courseOptional.get());
        Lesson createdLesson = lessonService.save(newLesson);

        URI locationOfCreatedLesson = uriBuilder.path("/api/v1/courses/{courseId}/lessons/{lessonId}")
                .buildAndExpand(courseId, createdLesson.getId()).toUri();

        return ResponseEntity.created(locationOfCreatedLesson).build();
    }

    @Operation(summary = "Update a lesson by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson successfully updated", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Lesson.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)

    })
    @PutMapping("/{lessonId}")
    public ResponseEntity<Lesson> update(@PathVariable Long courseId, @PathVariable Long lessonId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Lesson to update", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LessonDTO.class))) @Valid @RequestBody LessonDTO lesson) {
        logger.debug("Updating lesson with id: " + lessonId + " for course with id: " + courseId);

        Optional<Course> courseOptional = courseService.findById(courseId);
        if (!courseOptional.isPresent()) {
            logger.error("Course with id: " + courseId + " not found");
            return ResponseEntity.notFound().build();
        }

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.error("Lesson with id: " + lessonId + " not found");
            return ResponseEntity.notFound().build();
        }

        Lesson lessonToUpdate = lessonOptional.get();

        lessonToUpdate.setCourse(courseOptional.get());
        lessonToUpdate.setId(lessonId);
        Lesson updatedLesson = lessonService.save(lessonToUpdate);
        return ResponseEntity.ok(updatedLesson);
    }

    @Operation(summary = "Delete a lesson by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lesson successfully deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> delete(@PathVariable Long courseId, @PathVariable Long lessonId) {
        logger.debug("Deleting lesson with id: " + lessonId + " for course with id: " + courseId);

        if (!courseService.existsById(courseId)) {
            logger.error("Course with id: " + courseId + " not found");
            return ResponseEntity.notFound().build();
        }

        if (!lessonService.existsById(lessonId)) {
            logger.error("Lesson with id: " + lessonId + " not found");
            return ResponseEntity.notFound().build();
        }

        lessonService.deleteById(lessonId);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        logger.error("Lesson is invalid: {}", errors);

        return errors;
    }
}
