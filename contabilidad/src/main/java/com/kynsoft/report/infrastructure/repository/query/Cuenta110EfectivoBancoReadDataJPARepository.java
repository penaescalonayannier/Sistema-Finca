package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Cuenta110EfectivoBanco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface Cuenta110EfectivoBancoReadDataJPARepository extends JpaRepository<Cuenta110EfectivoBanco, UUID>{
    Page<Cuenta110EfectivoBanco> findAll(Specification specification, Pageable pageable);
}
