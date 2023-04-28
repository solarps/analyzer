package com.duop.analyzer.repository;

import com.duop.analyzer.entity.Mark;
import com.duop.analyzer.entity.Sheet;
import com.duop.analyzer.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByStudentAndSheet(Student student, Sheet sheet);

    boolean existsByStudentAndSheet(Student student, Sheet sheet);
}
