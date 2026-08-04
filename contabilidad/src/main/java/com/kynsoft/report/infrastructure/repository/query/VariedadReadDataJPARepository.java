package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Variedad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface VariedadReadDataJPARepository extends JpaRepository<Variedad, UUID>, JpaSpecificationExecutor<Variedad> {
    @Override
    Page<Variedad> findAll(Specification specification, Pageable pageable);
}
