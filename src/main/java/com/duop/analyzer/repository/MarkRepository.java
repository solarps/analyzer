package com.duop.analyzer.repository;

import com.duop.analyzer.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Mark.MarkId> {
    List<Mark> getAllMarksForStudent(@Param("student_id") Long id);
}
