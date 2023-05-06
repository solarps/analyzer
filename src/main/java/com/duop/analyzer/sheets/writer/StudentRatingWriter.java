package com.duop.analyzer.sheets.writer;

import com.duop.analyzer.entity.Student;
import com.duop.analyzer.repository.SubjectMark;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface StudentRatingWriter {

    byte[] writeStudentRating(Map<Student, List<SubjectMark>> studentMarks) throws IOException;
}
