package com.duop.analyzer.sheets;

import com.duop.analyzer.entity.*;
import com.duop.analyzer.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SheetResultProcessor {
    private final Logger logger = LoggerFactory.getLogger("AnalyzerLogger");
    private final StudentRepository studentRepository;
    private final LectorRepository lectorRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final MarkRepository markRepository;
    private final SheetRepository sheetRepository;


    @Transactional
    public synchronized void saveResult(SheetReadResult result) {
        if (sheetRepository.existsByNumber(result.getSheet().getNumber())) {
            logger.warn("Sheet: {} already exists", result.getSheet().getNumber());
            return;
        }
        Lector lector = lectorRepository
                .findByName(result.getLector().getName())
                .orElseGet(() -> lectorRepository.save(result.getLector()));
        result.getSheet().setLector(lector);

        Lector controlLector = lectorRepository
                .findByName(result.getControlLector().getName())
                .orElseGet(() -> lectorRepository.save(result.getControlLector()));
        result.getSheet().setControlLector(controlLector);

        Subject subject = subjectRepository
                .findByName(result.getSubject().getName())
                .orElseGet(() -> subjectRepository.save(result.getSubject()));
        result.getSheet().setSubject(subject);

        Group group = groupRepository
                .findByNameAndNumber(result.getGroup().getName(), result.getGroup().getNumber())
                .orElseGet(() -> groupRepository.save(result.getGroup()));
        result.getStudentMarks().keySet().forEach(student -> student.setGroup(group));

        List<Student> studentsToSave = result.getStudentMarks().keySet().stream()
                .filter(student -> !studentRepository.existsByNameAndGroup(student.getName(), student.getGroup()))
                .toList();
        studentRepository.saveAll(studentsToSave);

        sheetRepository.save(result.getSheet());

        List<Mark> marksToSave = result.getStudentMarks().values().stream()
                .map(mark -> {
                    mark.setStudent(studentRepository.findByNameAndGroup(mark.getStudent().getName(), mark.getStudent().getGroup())
                            .orElse(mark.getStudent()));
                    return mark;
                })
                .toList();
        markRepository.saveAll(marksToSave);

        sheetRepository.save(result.getSheet());
    }
}
