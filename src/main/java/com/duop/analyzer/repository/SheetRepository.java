package com.duop.analyzer.repository;

import com.duop.analyzer.entity.Sheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SheetRepository extends JpaRepository<Sheet, Long> {
    boolean existsByNumber(Integer number);
}
