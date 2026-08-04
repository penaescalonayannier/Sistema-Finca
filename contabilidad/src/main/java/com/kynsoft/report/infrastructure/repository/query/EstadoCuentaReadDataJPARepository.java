package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.EstadoCuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EstadoCuentaReadDataJPARepository extends JpaRepository<EstadoCuenta, UUID>, JpaSpecificationExecutor<EstadoCuenta>{
    @Override
    Page<EstadoCuenta> findAll(Specification specification, Pageable pageable);
}
