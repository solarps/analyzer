package com.duop.analyzer.service;

import com.duop.analyzer.entity.Student;
import com.duop.analyzer.repository.MarkRepository;
import com.duop.analyzer.repository.StudentRepository;
import com.duop.analyzer.repository.SubjectMark;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final MarkRepository markRepository;

    public List<Student> getAllStudentsForFlow(String flow) {
        String[] strings = flow.split("-");
        String flowName = strings[0];
        String flowYear = strings[1].replace("X", "");
        return studentRepository.findAllByFlow(flowName, flowYear + "%");
    }

    public Map<Student, List<SubjectMark>> getAllStudentsMarks(List<Student> students) {
        Map<Student, List<SubjectMark>> studentMarks = new HashMap<>();
        for (Student student : students) {
            List<SubjectMark> studentMarkForSubject = markRepository.findLastStudentMarkForSubject(student.getId());
            studentMarks.put(student, studentMarkForSubject);
        }
        return studentMarks;
    }
}
