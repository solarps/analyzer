package com.duop.analyzer.service;

import com.duop.analyzer.entity.Student;
import com.duop.analyzer.entity.StudentEducationType;
import com.duop.analyzer.repository.SubjectMark;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RatingService {

    public Map<Student, List<SubjectMark>> computeRating(Map<Student, List<SubjectMark>> studentMarks) {
        return studentMarks.entrySet().stream()
                .sorted(Comparator.comparing(this::sortByEducationType)
                        .thenComparingDouble(this::sortByAverageMark)
                        .thenComparingDouble(this::sortByISPAverageMark)
                        .thenComparingDouble(this::sortByKPAverageMark)
                        .reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }

    private StudentEducationType sortByEducationType(Map.Entry<Student, List<SubjectMark>> entry) {
        return entry.getKey().getEducationType();
    }

    private double sortByAverageMark(Map.Entry<Student, List<SubjectMark>> entry) {
        return entry.getValue().stream()
                .mapToDouble(mark -> Double.parseDouble(mark.getMarkValue()))
                .average()
                .orElse(0);
    }

    private double sortByISPAverageMark(Map.Entry<Student, List<SubjectMark>> entry) {
        return entry.getValue().stream()
                .filter(mark -> "ІСП".equals(mark.getType()))
                .mapToDouble(mark -> Double.parseDouble(mark.getMarkValue()))
                .average()
                .orElse(0);
    }

    private double sortByKPAverageMark(Map.Entry<Student, List<SubjectMark>> entry) {
        return entry.getValue().stream()
                .filter(mark -> "КП".equals(mark.getType()) || "КР".equals(mark.getType()))
                .mapToDouble(mark -> Double.parseDouble(mark.getMarkValue()))
                .average()
                .orElse(0);
    }
}
