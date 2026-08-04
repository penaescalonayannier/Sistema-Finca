package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Prestamo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PrestamoReadDataJPARepository extends JpaRepository<Prestamo, UUID>, JpaSpecificationExecutor<Prestamo> {
    @Override
    Page<Prestamo> findAll(Specification specification, Pageable pageable);
}
