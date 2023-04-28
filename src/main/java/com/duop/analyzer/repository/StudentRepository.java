package com.duop.analyzer.repository;

import com.duop.analyzer.entity.Group;
import com.duop.analyzer.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByNameAndGroup(String name, Group group);

    boolean existsByNameAndGroup(String name, Group group);
}
