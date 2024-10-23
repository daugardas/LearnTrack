package com.learntrack.server.services;

import com.learntrack.server.models.Lesson;
import com.learntrack.server.repositories.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public Iterable<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    public Iterable<Lesson> findAllByCourseId(Long courseId) {
        return lessonRepository.findAllByCourseId(courseId);
    }

    public Optional<Lesson> findById(Long id) {
        return lessonRepository.findById(id);
    }

    public Lesson save(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    public void deleteById(Long id) {
        lessonRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return lessonRepository.existsById(id);
    }
}
