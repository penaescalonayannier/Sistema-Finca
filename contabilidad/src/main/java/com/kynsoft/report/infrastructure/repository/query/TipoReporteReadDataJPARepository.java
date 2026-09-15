package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.TipoReporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface TipoReporteReadDataJPARepository extends JpaRepository<TipoReporte, UUID>, JpaSpecificationExecutor<TipoReporte> {

    @Override
    Page<TipoReporte> findAll(Specification<TipoReporte> specification, Pageable pageable);

    Optional<TipoReporte> findByCodigo(String codigo);

    List<TipoReporte> findByActivoTrueOrderByOrdenAsc();

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, UUID id);
}
