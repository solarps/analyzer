package com.duop.analyzer.repository;

import com.duop.analyzer.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("select student from Student student where student.name = :name and student.group = :group")
    Optional<Student> findStudentByUniqueKey(@Param("name") String name, @Param("group") String group);
}
