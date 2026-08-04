package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Reporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ReporteReadDataJPARepository extends JpaRepository<Reporte, UUID>, JpaSpecificationExecutor<Reporte> {
    
    @Override
    Page<Reporte> findAll(Specification specification, Pageable pageable);
}