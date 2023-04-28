package com.duop.analyzer.sheets;

import com.duop.analyzer.entity.*;
import lombok.Data;

import java.util.Map;

@Data
public class SheetReadResult {
    private Sheet sheet;
    private Map<Student, Mark> studentMarks;
    private Lector lector;
    private Lector controlLector;
    private Group group;
    private Subject subject;

    public SheetReadResult(Sheet sheet, Map<Student, Mark> studentMarks, Lector lector,
                           Lector controlLector, Group group, Subject subject) {
        this.sheet = sheet;
        this.studentMarks = studentMarks;
        this.lector = lector;
        this.controlLector = controlLector;
        this.group = group;
        this.subject = subject;
        createReferences();
    }

    private void createReferences() {
        this.sheet.setLector(this.lector);
        this.sheet.setControlLector(this.controlLector);
        this.sheet.setSubject(this.subject);
        this.studentMarks.keySet().forEach(student -> student.setGroup(this.group));
        this.studentMarks.forEach(
                (key, value) -> {
                    key.setGroup(this.group);
                    value.setSheet(this.sheet);
                    value.setStudent(key);
                });
    }
}
