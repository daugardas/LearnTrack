package com.learntrack.server.converters;

import java.util.ArrayList;
import java.util.List;

import com.learntrack.server.dto.CourseRequestDTO;
import com.learntrack.server.dto.CourseResponseDTO;
import com.learntrack.server.models.Course;

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
