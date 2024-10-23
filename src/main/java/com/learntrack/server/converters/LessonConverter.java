package com.learntrack.server.converters;

import java.util.ArrayList;
import java.util.List;

import com.learntrack.server.dto.LessonRequestDTO;
import com.learntrack.server.dto.LessonResponseDTO;
import com.learntrack.server.models.Lesson;

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
