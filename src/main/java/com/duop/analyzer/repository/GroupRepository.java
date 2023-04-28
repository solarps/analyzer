package com.duop.analyzer.repository;

import com.duop.analyzer.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByNameAndNumber(String name, Short number);

    Optional<Group> findByNameAndNumber(String name, Short number);
}
