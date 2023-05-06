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

    @Query(value = "select sub.name as subjectName, m.mark as markValue, ft.name as type\n" +
            "from marks as m\n" +
            "         join students s on s.id = m.student_id\n" +
            "         join sheets sh on sh.id = m.sheet_id\n" +
            "         join subjects sub on sub.id = sh.subject_id\n" +
            "         join form_types ft on sh.form_id = ft.id\n" +
            "where s.id = ?", nativeQuery = true)
    List<SubjectMark> findStudentMarkForSubject(Long studentId);
}
