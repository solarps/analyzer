package com.duop.analyzer.repository;

import com.duop.analyzer.entity.StudentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Long> {
    boolean existsByName(String name);

    Optional<StudentDetails> findByName(String name);
}
