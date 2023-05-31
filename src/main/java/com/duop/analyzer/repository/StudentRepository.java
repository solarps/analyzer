package com.duop.analyzer.repository;

import com.duop.analyzer.entity.Group;
import com.duop.analyzer.entity.Student;
import com.duop.analyzer.entity.StudentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByDetailsAndGroup(StudentDetails details, Group group);

    boolean existsByDetailsAndGroup(StudentDetails details, Group group);

    @Query(value = "select s from Student s\n" +
            "         join s.group g where g.name = :flowName and cast(g.number as string ) like :flowYear")
    List<Student> findAllByFlow(String flowName, String flowYear);

}
