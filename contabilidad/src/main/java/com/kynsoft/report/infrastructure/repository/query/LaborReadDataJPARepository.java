package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Labor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface LaborReadDataJPARepository extends JpaRepository<Labor, UUID>, JpaSpecificationExecutor<Labor> {
    @Override
    @EntityGraph(attributePaths = "cargosRequeridos")
    Page<Labor> findAll(Specification specification, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "cargosRequeridos")
    Optional<Labor> findById(UUID id);
}
