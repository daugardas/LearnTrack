package com.learntrack.resourceserver.controllers;

import com.learntrack.resourceserver.annotations.CurrentUserIdResolver;
import com.learntrack.resourceserver.converters.LessonConverter;
import com.learntrack.resourceserver.dto.LessonRequestDTO;
import com.learntrack.resourceserver.dto.LessonResponseDTO;
import com.learntrack.resourceserver.exceptions.ResourceNotFoundException;
import com.learntrack.resourceserver.models.Course;
import com.learntrack.resourceserver.models.Lesson;
import com.learntrack.resourceserver.services.CourseService;
import com.learntrack.resourceserver.services.LessonService;
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
    // @formatter:off
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Successfully retrieved list of lessons", 
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                    array = @ArraySchema(
                        schema = @Schema(implementation = LessonResponseDTO.class)
                    )
                )
            ),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    // @formatter:on
    @GetMapping()
    public ResponseEntity<Iterable<LessonResponseDTO>> findAll(
            // @formatter:off
        @PathVariable Long courseId,
        @CurrentSecurityContext(expression = "authentication") Authentication authentication
        // @formatter:on
    ) {

        Long userId = CurrentUserIdResolver.getCurrentUserId(authentication);
        if (userId == null) {
            logger.info("Anonymous user is trying to get all lessons for course with id: " + courseId);
        } else {
            logger.info("Principal '{}' is trying to get all lessons for course with id: " + courseId, userId);
        }

        if (!courseService.existsById(courseId)) {
            logger.info("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Iterable<LessonResponseDTO> lessons = LessonConverter.convertToLessonResponseDTOList(lessonService.findAllByCourseId(courseId));
        return ResponseEntity.ok(lessons);
    }

    @Operation(summary = "Find lesson by id")
    // @formatter:off
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved lesson", 
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                    schema = @Schema(implementation = LessonResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content),
    })
    // @formatter:on
    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonResponseDTO> findById(
            // @formatter:off
        @PathVariable Long courseId, 
        @PathVariable Long lessonId,
        @CurrentSecurityContext(expression = "authentication") Authentication authentication
        // @formatter:on
    ) {

        Long userId = CurrentUserIdResolver.getCurrentUserId(authentication);
        if (userId == null) {
            logger.info("Anonymous user is trying to get lesson with id: " + lessonId + " for course with id: " + courseId);
        } else {
            logger.info("Principal '{}' is trying to get lesson with id: " + lessonId + " for course with id: " + courseId, userId);
        }
        if (!courseService.existsById(courseId)) {
            logger.info("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.info("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        Lesson lesson = lessonOptional.get();
        if (!lesson.getCourse().getId().equals(courseId)) {
            logger.info("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        LessonResponseDTO lessonResponseDTO = LessonConverter.convertToLessonResponseDTO(lesson);

        return ResponseEntity.ok(lessonResponseDTO);
    }

    @Operation(summary = "Create a new lesson for a course")
    // @formatter:off
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "201", 
                description = "Successfully created lesson", 
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                    schema = @Schema(implementation = LessonResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    // @formatter:on
    @PostMapping
    public ResponseEntity<LessonResponseDTO> create(
            // @formatter:off
        @PathVariable Long courseId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, 
            description = "Lesson to create", 
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = LessonRequestDTO.class)
            )) @Valid @RequestBody LessonRequestDTO lesson,
            UriComponentsBuilder uriBuilder,
        @CurrentSecurityContext(expression = "authentication") Authentication authentication
        // @formatter:on
    ) {

        Long userId = CurrentUserIdResolver.getCurrentUserId(authentication);
        if (userId == null) {
            logger.info("Anonymous user is trying to create lesson for course with id: " + courseId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            logger.info("Principal '{}' is trying to create lesson for course with id: " + courseId, userId);
            Jwt jwt = (Jwt) authentication.getPrincipal();
            @SuppressWarnings("unchecked") List<String> roles = (List<String>) jwt.getClaims().get("roles");
            if (!roles.contains("ROLE_LECTURER") && !roles.contains("ROLE_ADMIN")) {
                logger.info("Principal '{}' is not an admin or lecturer", userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        Optional<Course> courseOptional = courseService.findById(courseId);
        if (!courseOptional.isPresent()) {
            logger.info("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Course course = courseOptional.get();

        Lesson newLesson = new Lesson(lesson, course.getOwnerId());

        newLesson.setCourse(course);
        Lesson createdLesson = lessonService.save(newLesson);

        LessonResponseDTO lessonResponseDTO = LessonConverter.convertToLessonResponseDTO(createdLesson);

        URI locationOfCreatedLesson = uriBuilder.path("/api/v1/courses/{courseId}/lessons/{lessonId}").buildAndExpand(courseId, createdLesson.getId()).toUri();

        return ResponseEntity.created(locationOfCreatedLesson).body(lessonResponseDTO);
    }

    @Operation(summary = "Update a lesson by id")
    // @formatter:off
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Lesson successfully updated", 
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                    schema = @Schema(implementation = LessonResponseDTO.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    // @formatter:on
    @PutMapping("/{lessonId}")
    public ResponseEntity<LessonResponseDTO> update(
            // @formatter:off
        @PathVariable Long courseId, 
        @PathVariable Long lessonId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, 
            description = "Lesson to update", 
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = LessonRequestDTO.class)
            )
        ) @Valid @RequestBody LessonRequestDTO lesson,
        @CurrentSecurityContext(expression = "authentication") Authentication authentication
        // @formatter:on
    ) {

        Long userId = CurrentUserIdResolver.getCurrentUserId(authentication);
        if (userId == null) {
            logger.info("Anonymous user is trying to update lesson with id: " + lessonId + " for course with id: " + courseId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            logger.info("Principal '{}' is trying to update lesson with id: " + lessonId + " for course with id: " + courseId, userId);
            Jwt jwt = (Jwt) authentication.getPrincipal();
            @SuppressWarnings("unchecked") List<String> roles = (List<String>) jwt.getClaims().get("roles");
            if (!roles.contains("ROLE_LECTURER") && !roles.contains("ROLE_ADMIN")) {
                logger.info("Principal '{}' is not an admin or lecturer", userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        Optional<Course> courseOptional = courseService.findById(courseId);
        if (!courseOptional.isPresent()) {
            logger.info("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Course course = courseOptional.get();
        if (!course.getOwnerId().equals(userId)) {
            logger.info("Principal '{}' is not the owner of the course '{}'", userId, courseId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.info("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }


        Lesson lessonToUpdate = lessonOptional.get();
        if (!lessonToUpdate.getCourse().getId().equals(courseId)) {
            logger.info("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        if (lesson.getTitle() != null) lessonToUpdate.setTitle(lesson.getTitle());

        if (lesson.getDescription() != null) lessonToUpdate.setDescription(lesson.getDescription());
        Lesson updatedLesson = lessonService.save(lessonToUpdate);

        LessonResponseDTO lessonResponseDTO = LessonConverter.convertToLessonResponseDTO(updatedLesson);

        return ResponseEntity.ok(lessonResponseDTO);
    }

    @Operation(summary = "Delete a lesson by id")
    // @formatter:off
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "204",
                description = "Lesson successfully deleted", 
                content = @Content
            ),
            @ApiResponse(responseCode = "404", description = "Lesson not found", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    // @formatter:on    
    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> delete(
            // @formatter:off
        @PathVariable Long courseId, 
        @PathVariable Long lessonId,
        @CurrentSecurityContext(expression = "authentication") Authentication authentication
        // @formatter:on
    ) {

        Long userId = CurrentUserIdResolver.getCurrentUserId(authentication);
        if (userId == null) {
            logger.info("Anonymous user is trying to delete lesson with id: " + lessonId + " for course with id: " + courseId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            logger.info("Principal '{}' is trying to delete lesson with id: " + lessonId + " for course with id: " + courseId, userId);
            Jwt jwt = (Jwt) authentication.getPrincipal();
            @SuppressWarnings("unchecked") List<String> roles = (List<String>) jwt.getClaims().get("roles");
            if (!roles.contains("ROLE_LECTURER") && !roles.contains("ROLE_ADMIN")) {
                logger.info("Principal '{}' is not an admin or lecturer", userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        Optional<Course> courseOptional = courseService.findById(courseId);
        if (!courseOptional.isPresent()) {
            logger.info("Course with id: " + courseId + " not found");
            throw new ResourceNotFoundException("Course with id: " + courseId + " not found");
        }

        Course course = courseOptional.get();
        if (!course.getOwnerId().equals(userId)) {
            logger.info("Principal '{}' is not the owner of the course '{}'", userId, courseId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Lesson> lessonOptional = lessonService.findById(lessonId);
        if (!lessonOptional.isPresent()) {
            logger.info("Lesson with id: " + lessonId + " not found");
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        Lesson lesson = lessonOptional.get();
        if (!lesson.getCourse().getId().equals(courseId)) {
            logger.info("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
            throw new ResourceNotFoundException("Lesson with id: " + lessonId + " not found for course with id: " + courseId);
        }

        lessonService.deleteById(lessonId);
        return ResponseEntity.noContent().build();
    }
}
