package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Finca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface FincaReadDataJPARepository extends JpaRepository<Finca, UUID>, JpaSpecificationExecutor<Finca> {
    
    @Override
    Page<Finca> findAll(Specification specification, Pageable pageable);
    
    Optional<Finca> findByCode(String code);
    
    Optional<Finca> findByName(String name);
}