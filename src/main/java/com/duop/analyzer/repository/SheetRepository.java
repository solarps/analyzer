package com.duop.analyzer.repository;

import com.duop.analyzer.entity.Sheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SheetRepository extends JpaRepository<Sheet, Long> {
    boolean existsByNumber(Integer number);
}
