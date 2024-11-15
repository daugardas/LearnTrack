package com.learntrack.resourceserver.converters;

import com.learntrack.resourceserver.dto.LessonRequestDTO;
import com.learntrack.resourceserver.dto.LessonResponseDTO;
import com.learntrack.resourceserver.models.Lesson;

import java.util.ArrayList;
import java.util.List;

public class LessonConverter {
    public static LessonResponseDTO convertToLessonResponseDTO(Lesson lesson) {
        LessonResponseDTO lessonResponseDTO = new LessonResponseDTO();
        lessonResponseDTO.setId(lesson.getId());
        lessonResponseDTO.setTitle(lesson.getTitle());
        lessonResponseDTO.setDescription(lesson.getDescription());
        lessonResponseDTO.setCourseId(lesson.getCourse().getId());
        return lessonResponseDTO;
    }

    public static Lesson convertToEntity(LessonRequestDTO lessonRequestDTO) {
        Lesson lesson = new Lesson();
        lesson.setTitle(lessonRequestDTO.getTitle());
        lesson.setDescription(lessonRequestDTO.getDescription());
        return lesson;
    }

    public static List<LessonResponseDTO> convertToLessonResponseDTOList(Iterable<Lesson> lessons) {
        List<LessonResponseDTO> lessonResponseDTOList = new ArrayList<>();
        for (Lesson lesson : lessons) {
            lessonResponseDTOList.add(convertToLessonResponseDTO(lesson));
        }
        return lessonResponseDTOList;
    }
}
