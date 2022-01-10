package com.excel_translator_service.server.model.excel_translator_header.repository;

import java.util.Optional;
import java.util.UUID;

import com.excel_translator_service.server.model.excel_translator_header.entity.ExcelTranslatorHeaderEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcelTranslatorHeaderRepository extends JpaRepository<ExcelTranslatorHeaderEntity, Integer> {
    
    public Optional<ExcelTranslatorHeaderEntity> findById(UUID id);
}
