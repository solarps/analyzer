package com.duop.analyzer.repository;

import com.duop.analyzer.entity.Mark;
import com.duop.analyzer.entity.Sheet;
import com.duop.analyzer.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByStudentAndSheet(Student student, Sheet sheet);

    boolean existsByStudentAndSheet(Student student, Sheet sheet);

    List<Mark> findAllByStudent(Student student);

    @Query(value = """
            SELECT sub.name AS subjectName, m.mark AS markValue, ft.name AS type
            FROM marks AS m
                     JOIN students s ON s.id = m.student_id
                     JOIN sheets sh ON sh.id = m.sheet_id
                     JOIN subjects sub ON sub.id = sh.subject_id
                     JOIN form_types ft ON sh.form_id = ft.id
            WHERE s.id = ?
              AND sh.course = (SELECT MAX(course)
                               FROM sheets
                               where m.student_id = s.id)""", nativeQuery = true)
    List<SubjectMark> findLastStudentMarkForSubject(Long studentId);
}
