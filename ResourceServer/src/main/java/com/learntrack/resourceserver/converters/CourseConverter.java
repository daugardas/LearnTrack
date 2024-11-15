package com.learntrack.resourceserver.converters;

import com.learntrack.resourceserver.dto.CourseRequestDTO;
import com.learntrack.resourceserver.dto.CourseResponseDTO;
import com.learntrack.resourceserver.models.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseConverter {
    public static CourseResponseDTO convertToCourseResponseDTO(Course course) {
        CourseResponseDTO courseResponseDTO = new CourseResponseDTO();
        courseResponseDTO.setId(course.getId());
        courseResponseDTO.setName(course.getName());
        courseResponseDTO.setDescription(course.getDescription());
        return courseResponseDTO;
    }

    public static Course convertToEntity(CourseRequestDTO courseRequestDTO) {
        Course course = new Course();
        course.setName(courseRequestDTO.getName());
        course.setDescription(courseRequestDTO.getDescription());
        return course;
    }

    public static List<CourseResponseDTO> convertToCourseResponseDTOList(Iterable<Course> courses) {
        List<CourseResponseDTO> courseResponseDTOList = new ArrayList<>();
        for (Course course : courses) {
            courseResponseDTOList.add(convertToCourseResponseDTO(course));
        }
        return courseResponseDTOList;
    }
}
