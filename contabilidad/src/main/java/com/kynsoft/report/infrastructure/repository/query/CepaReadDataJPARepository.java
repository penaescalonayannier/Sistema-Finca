package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Cepa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CepaReadDataJPARepository extends JpaRepository<Cepa, UUID>, JpaSpecificationExecutor<Cepa> {
    @Override
    Page<Cepa> findAll(Specification specification, Pageable pageable);
}
