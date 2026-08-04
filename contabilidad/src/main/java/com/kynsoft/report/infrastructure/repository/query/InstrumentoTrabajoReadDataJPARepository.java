package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.InstrumentoTrabajo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface InstrumentoTrabajoReadDataJPARepository extends JpaRepository<InstrumentoTrabajo, UUID>, JpaSpecificationExecutor<InstrumentoTrabajo> {
    @Override
    Page<InstrumentoTrabajo> findAll(Specification specification, Pageable pageable);
}
