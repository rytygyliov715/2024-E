package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.ExcelColumnMapping;

public interface ExcelColumnMappingRepository extends JpaRepository<ExcelColumnMapping, Integer> {
}
