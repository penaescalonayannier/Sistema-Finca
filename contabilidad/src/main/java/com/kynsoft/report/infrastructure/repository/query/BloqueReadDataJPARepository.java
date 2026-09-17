package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Bloque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface BloqueReadDataJPARepository extends JpaRepository<Bloque, UUID>, JpaSpecificationExecutor<Bloque> {
    @Override
    @EntityGraph(attributePaths = "finca")
    java.util.Optional<Bloque> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = "finca")
    Page<Bloque> findAll(Specification specification, Pageable pageable);
}
