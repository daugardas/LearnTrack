package com.learntrack.server.controllers;

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

import com.learntrack.server.converters.LessonConverter;
import com.learntrack.server.dto.LessonRequestDTO;
import com.learntrack.server.dto.LessonResponseDTO;
import com.learntrack.server.exceptions.ResourceNotFoundException;
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
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of lessons", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = LessonResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)

    })
    @GetMapping()
    public ResponseEntity<Iterable<LessonResponseDTO>> findAll(@PathVariable Long courseId) {
        logger.info("Finding all lessons for course with id: " + courseId);

        if (!courseService.existsById(courseId)) {
            logger.info("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Iterable<LessonResponseDTO> lessons = LessonConverter
                .convertToLessonResponseDTOList(lessonService.findAllByCourseId(courseId));
        return ResponseEntity.ok(lessons);
    }

    @Operation(summary = "Find lesson by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved lesson", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LessonResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonResponseDTO> findById(@PathVariable Long courseId, @PathVariable Long lessonId) {
        logger.info("Finding lesson with id: " + lessonId + " for course with id: " + courseId);

        if (!courseService.existsById(courseId)) {
            logger.info("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.info("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException(
                    "Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        Lesson lesson = lessonOptional.get();
        if (!lesson.getCourse().getId().equals(courseId)) {
            logger.info("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
            throw new ResourceNotFoundException(
                    "Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        LessonResponseDTO lessonResponseDTO = LessonConverter.convertToLessonResponseDTO(lesson);

        return ResponseEntity.ok(lessonResponseDTO);
    }

    @Operation(summary = "Create a new lesson for a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created lesson", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LessonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<LessonResponseDTO> create(@PathVariable Long courseId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Lesson to create", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LessonRequestDTO.class))) @Valid @RequestBody LessonRequestDTO lesson,
            UriComponentsBuilder uriBuilder) {
        logger.info("Creating lesson for course with id: " + courseId);

        Optional<Course> courseOptional = courseService.findById(courseId);
        if (!courseOptional.isPresent()) {
            logger.info("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Lesson newLesson = new Lesson(lesson);

        newLesson.setCourse(courseOptional.get());
        Lesson createdLesson = lessonService.save(newLesson);

        LessonResponseDTO lessonResponseDTO = LessonConverter.convertToLessonResponseDTO(createdLesson);

        URI locationOfCreatedLesson = uriBuilder.path("/api/v1/courses/{courseId}/lessons/{lessonId}")
                .buildAndExpand(courseId, createdLesson.getId()).toUri();

        return ResponseEntity.created(locationOfCreatedLesson).body(lessonResponseDTO);
    }

    @Operation(summary = "Update a lesson by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson successfully updated", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LessonResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)

    })
    @PutMapping("/{lessonId}")
    public ResponseEntity<LessonResponseDTO> update(@PathVariable Long courseId, @PathVariable Long lessonId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Lesson to update", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LessonRequestDTO.class))) @Valid @RequestBody LessonRequestDTO lesson) {
        logger.info("Updating lesson with id: " + lessonId + " for course with id: " + courseId);

        Optional<Course> courseOptional = courseService.findById(courseId);
        if (!courseOptional.isPresent()) {
            logger.info("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.info("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException(
                    "Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        Lesson lessonToUpdate = lessonOptional.get();
        if (!lessonToUpdate.getCourse().getId().equals(courseId)) {
            logger.info("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
            throw new ResourceNotFoundException(
                    "Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        if (lesson.getTitle() != null)
            lessonToUpdate.setTitle(lesson.getTitle());

        if (lesson.getDescription() != null)
            lessonToUpdate.setDescription(lesson.getDescription());
        Lesson updatedLesson = lessonService.save(lessonToUpdate);

        LessonResponseDTO lessonResponseDTO = LessonConverter.convertToLessonResponseDTO(updatedLesson);

        return ResponseEntity.ok(lessonResponseDTO);
    }

    @Operation(summary = "Delete a lesson by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lesson successfully deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> delete(@PathVariable Long courseId, @PathVariable Long lessonId) {
        logger.info("Deleting lesson with id: " + lessonId + " for course with id: " + courseId);

        if (!courseService.existsById(courseId)) {
            logger.info("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.info("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException(
                    "Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        Lesson lesson = lessonOptional.get();
        if (!lesson.getCourse().getId().equals(courseId)) {
            logger.info("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
            throw new ResourceNotFoundException(
                    "Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        lessonService.deleteById(lessonId);
        return ResponseEntity.noContent().build();
    }
}
