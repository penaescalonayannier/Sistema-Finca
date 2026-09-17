package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Grupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface GrupoReadDataJPARepository extends JpaRepository<Grupo, UUID>, JpaSpecificationExecutor<Grupo> {
    /** Grupo.toAggregate() muestra jefe y trabajadores al serializar los resultados CQRS. */
    @Override
    @EntityGraph(attributePaths = {"jefe", "trabajadores"})
    java.util.Optional<Grupo> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"jefe", "trabajadores"})
    Page<Grupo> findAll(Specification specification, Pageable pageable);
}
