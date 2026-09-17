package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.DeudaTrabajador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface DeudaTrabajadorReadDataJPARepository extends JpaRepository<DeudaTrabajador, UUID>, JpaSpecificationExecutor<DeudaTrabajador> {
    @Override
    @EntityGraph(attributePaths = {"trabajador"})
    Page<DeudaTrabajador> findAll(Specification specification, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"trabajador"})
    Optional<DeudaTrabajador> findById(UUID id);

    @EntityGraph(attributePaths = {"trabajador"})
    Optional<DeudaTrabajador> findByTrabajadorId(UUID trabajadorId);
}
