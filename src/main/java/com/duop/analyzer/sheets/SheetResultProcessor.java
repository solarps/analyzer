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
    private final StudentDetailsRepository studentDetailsRepository;


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
        logger.trace("Lector saved");

        Lector controlLector = lectorRepository
                .findByName(result.getControlLector().getName())
                .orElseGet(() -> lectorRepository.save(result.getControlLector()));
        result.getSheet().setControlLector(controlLector);
        logger.trace("Control lector saved");

        Subject subject = subjectRepository
                .findByName(result.getSubject().getName())
                .orElseGet(() -> subjectRepository.save(result.getSubject()));
        result.getSheet().setSubject(subject);
        logger.trace("Control lector saved");

        Group group = groupRepository
                .findByNameAndNumber(result.getGroup().getName(), result.getGroup().getNumber())
                .orElseGet(() -> groupRepository.save(result.getGroup()));
        result.getStudentMarks().keySet().forEach(student -> student.setGroup(group));
        logger.trace("Group saved");

        List<Student> studentsToSave = result.getStudentMarks().keySet().stream()
                .peek(student -> student.setEducationType(StudentEducationType.generateType()))
                .map(student -> {
                    student.setDetails(studentDetailsRepository.findByName(student.getDetails().getName())
                            .orElseGet(() -> studentDetailsRepository.save(student.getDetails())));
                    return student;
                }).filter(student -> !studentRepository.existsByDetailsAndGroup(student.getDetails(), student.getGroup()))
                .toList();
        studentRepository.saveAll(studentsToSave);
        logger.trace("Students saved");

        sheetRepository.save(result.getSheet());
        logger.trace("Sheet saved");

        List<Mark> marksToSave = result.getStudentMarks().values().stream()
                .map(mark -> {
                    mark.setStudent(studentRepository.findByDetailsAndGroup(mark.getStudent().getDetails(), mark.getStudent().getGroup())
                            .orElse(mark.getStudent()));
                    return mark;
                })
                .toList();
        markRepository.saveAll(marksToSave);
        logger.trace("Marks saved");
    }
}
