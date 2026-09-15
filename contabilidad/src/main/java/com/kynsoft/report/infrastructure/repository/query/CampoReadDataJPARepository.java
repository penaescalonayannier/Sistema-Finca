package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Campo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface CampoReadDataJPARepository extends JpaRepository<Campo, UUID>, JpaSpecificationExecutor<Campo> {
    @Override
    Page<Campo> findAll(Specification specification, Pageable pageable);

    List<Campo> findByBloqueId(UUID bloqueId);
}
