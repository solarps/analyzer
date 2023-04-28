package com.duop.analyzer.repository;

import com.duop.analyzer.entity.Lector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LectorRepository extends JpaRepository<Lector, Long> {
    Optional<Lector> findByName(String name);

    boolean existsByName(String name);
}
