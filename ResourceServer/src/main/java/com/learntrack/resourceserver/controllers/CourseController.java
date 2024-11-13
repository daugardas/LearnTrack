package com.learntrack.resourceserver.controllers;

import com.learntrack.resourceserver.converters.CourseConverter;
import com.learntrack.resourceserver.dto.CourseRequestDTO;
import com.learntrack.resourceserver.dto.CourseResponseDTO;
import com.learntrack.resourceserver.exceptions.ResourceNotFoundException;
import com.learntrack.resourceserver.models.Course;
import com.learntrack.resourceserver.services.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Course", description = "Endpoints for managing course information")
@Validated
public class CourseController {

    private final CourseService courseService;

    Logger logger = LoggerFactory.getLogger(CourseController.class);

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "Find all courses")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseResponseDTO.class)))})})
    @GetMapping
    @PreAuthorize("hasAuthority('course:read')")
    public ResponseEntity<Iterable<CourseResponseDTO>> findAll(@CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        logger.info("Principal '{}' is trying to get all courses", authentication.getName());
        Iterable<CourseResponseDTO> courses = CourseConverter.convertToCourseResponseDTOList(courseService.findAll());
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Find course by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved course", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CourseResponseDTO.class))}), @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)})
    @GetMapping("/{requestedId}")
    @PreAuthorize("hasAuthority('course:read')")
    public ResponseEntity<CourseResponseDTO> findById(@CurrentSecurityContext(expression = "authentication") Authentication authentication, @Parameter(description = "id of a course to be searched") @PathVariable Long requestedId) {
        logger.info("Principal '{}' is trying to get course '{}'", authentication.getName(), requestedId);
        Optional<Course> courseOptional = courseService.findById(requestedId);
        if (courseOptional.isEmpty()) {
            logger.info("Course not found");
            throw new ResourceNotFoundException("Course with id " + requestedId + " not found");
        }

        CourseResponseDTO courseResponseDTO = CourseConverter.convertToCourseResponseDTO(courseOptional.get());

        logger.info("Course found: {}", courseOptional.get());
        return ResponseEntity.ok(courseResponseDTO);
    }

    @Operation(summary = "Create a new course")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Course successfully created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CourseResponseDTO.class))}), @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),})
    @PostMapping
    @PreAuthorize("hasAuthority('course:write')")
    public ResponseEntity<CourseResponseDTO> create(@CurrentSecurityContext(expression = "authentication") Authentication authentication, @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Course to create", content = @Content(mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CourseRequestDTO.class), examples = {@ExampleObject(name = "An example request with name and description", value = "{ \"name\": \"New course\", \"description\": \"Course description\"}"), @ExampleObject(name = "An example request with only required fields", value = "{ \"name\": \"Another course\"}"),})) @Valid @RequestBody CourseRequestDTO course, UriComponentsBuilder uriBuilder) {
        logger.info("Principal '{}' is trying to create a new course '{}'", authentication.getName(), course);
        Course savedCourse = courseService.save(new Course(course));

        CourseResponseDTO courseResponseDTO = CourseConverter.convertToCourseResponseDTO(savedCourse);

        URI locationOfSavedCourse = uriBuilder.path("/api/v1/courses/{id}").buildAndExpand(savedCourse.getId()).toUri();

        return ResponseEntity.created(locationOfSavedCourse).body(courseResponseDTO);
    }

    @Operation(summary = "Update a course by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Course successfully updated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CourseResponseDTO.class))}), @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content), @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)})
    @PutMapping("/{requestedId}")
    public ResponseEntity<CourseResponseDTO> updateById(@CurrentSecurityContext(expression = "authentication") Authentication authentication, @Parameter(description = "id of a course to be updated") @PathVariable Long requestedId, @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Course to update", content = @Content(mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CourseRequestDTO.class))) @Valid @RequestBody CourseRequestDTO course) {
        logger.info("Principal '{}' is trying to update course '{}'", authentication.getName(), requestedId);

        Optional<Course> courseOptional = courseService.findById(requestedId);
        if (courseOptional.isEmpty()) {
            logger.info("Course not found");
            throw new ResourceNotFoundException("Course with id " + requestedId + " not found");
        }

        Course courseToUpdate = courseOptional.get();
        if (course.getName() != null) courseToUpdate.setName(course.getName());

        if (course.getDescription() != null) courseToUpdate.setDescription(course.getDescription());
        Course updatedCourse = courseService.save(courseToUpdate);

        logger.info("Course updated: '{}'", updatedCourse);

        CourseResponseDTO courseResponseDTO = CourseConverter.convertToCourseResponseDTO(updatedCourse);
        return ResponseEntity.ok(courseResponseDTO);
    }

    @Operation(summary = "Delete a course by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Course successfully deleted", content = @Content), @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)})
    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteById(@CurrentSecurityContext(expression = "authentication") Authentication authentication, @Parameter(description = "id of a course to be deleted") @PathVariable Long requestedId) {
        logger.info("Principal '{}' is trying to delete course '{}'", authentication.getName(), requestedId);
        if (!courseService.existsById(requestedId)) {
            logger.info("Course '{}' not found", requestedId);
            throw new ResourceNotFoundException("Course with id " + requestedId + " not found");
        }

        courseService.deleteById(requestedId);
        logger.info("Course '{}' deleted", requestedId);
        return ResponseEntity.noContent().build();
    }
}
