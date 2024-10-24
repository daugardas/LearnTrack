package com.learntrack.server.controllers;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.learntrack.server.converters.CourseConverter;
import com.learntrack.server.dto.CourseRequestDTO;
import com.learntrack.server.dto.CourseResponseDTO;
import com.learntrack.server.exceptions.ResourceNotFoundException;
import com.learntrack.server.models.Course;
import com.learntrack.server.services.CourseService;

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseResponseDTO.class)))
            })
    })
    @GetMapping
    public ResponseEntity<Iterable<CourseResponseDTO>> findAll() {
        logger.info("Finding all courses");
        Iterable<CourseResponseDTO> courses = CourseConverter
                .convertToCourseResponseDTOList(courseService.findAll());
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Find course by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved course", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CourseResponseDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })

    @GetMapping("/{requestedId}")
    public ResponseEntity<CourseResponseDTO> findById(
            @Parameter(description = "id of a course to be searched") @PathVariable Long requestedId) {
        logger.info("Finding course with id: {}", requestedId);
        Optional<Course> courseOptional = courseService.findById(requestedId);
        if (!courseOptional.isPresent()) {
            logger.info("Course not found");
            throw new ResourceNotFoundException("Course with id " + requestedId + " not found");
        }

        CourseResponseDTO courseResponseDTO = CourseConverter
                .convertToCourseResponseDTO(courseOptional.get());

        logger.info("Course found: {}", courseOptional.get());
        return ResponseEntity.ok(courseResponseDTO);
    }

    @Operation(summary = "Create a new course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Course successfully created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CourseResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
    })
    @PostMapping
    public ResponseEntity<CourseResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Course to create", content = @Content(mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CourseRequestDTO.class), examples = {
                    @ExampleObject(name = "An example request with name and description", value = "{ \"name\": \"New course\", \"description\": \"Course description\"}"),
                    @ExampleObject(name = "An example request with only required fields", value = "{ \"name\": \"Another course\"}"),
            })) @Valid @RequestBody CourseRequestDTO course,
            UriComponentsBuilder uriBuilder) {
        logger.info("Creating course: {}", course);
        Course savedCourse = courseService.save(new Course(course));

        CourseResponseDTO courseResponseDTO = CourseConverter.convertToCourseResponseDTO(savedCourse);

        URI locationOfSavedCourse = uriBuilder.path("/api/v1/courses/{id}").buildAndExpand(savedCourse.getId())
                .toUri();

        return ResponseEntity.created(locationOfSavedCourse).body(courseResponseDTO);
    }

    @Operation(summary = "Update a course by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course successfully updated", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CourseResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @PutMapping("/{requestedId}")
    public ResponseEntity<CourseResponseDTO> updateById(
            @Parameter(description = "id of a course to be updated") @PathVariable Long requestedId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Course to update", content = @Content(mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CourseRequestDTO.class))) @Valid @RequestBody CourseRequestDTO course) {
        logger.info("Updating course with id: {}", requestedId);

        Optional<Course> courseOptional = courseService.findById(requestedId);
        if (!courseOptional.isPresent()) {
            logger.info("Course not found");
            throw new ResourceNotFoundException("Course with id " + requestedId + " not found");
        }

        Course courseToUpdate = courseOptional.get();
        if (course.getName() != null)
            courseToUpdate.setName(course.getName());

        if (course.getDescription() != null)
            courseToUpdate.setDescription(course.getDescription());
        Course updatedCourse = courseService.save(courseToUpdate);

        logger.info("Course updated: {}", updatedCourse);

        CourseResponseDTO courseResponseDTO = CourseConverter.convertToCourseResponseDTO(updatedCourse);
        return ResponseEntity.ok(courseResponseDTO);
    }

    @Operation(summary = "Delete a course by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Course successfully deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "id of a course to be deleted") @PathVariable Long requestedId) {
        logger.info("Deleting course with id: {}", requestedId);
        if (!courseService.existsById(requestedId)) {
            logger.info("Course not found");
            throw new ResourceNotFoundException("Course with id " + requestedId + " not found");
        }

        courseService.deleteById(requestedId);
        return ResponseEntity.noContent().build();
    }
}
