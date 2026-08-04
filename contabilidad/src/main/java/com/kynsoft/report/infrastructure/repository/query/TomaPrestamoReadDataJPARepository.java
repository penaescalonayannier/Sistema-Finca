package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.TomaPrestamo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface TomaPrestamoReadDataJPARepository extends JpaRepository<TomaPrestamo, UUID>, JpaSpecificationExecutor<TomaPrestamo> {
    @Override
    Page<TomaPrestamo> findAll(Specification specification, Pageable pageable);
}
