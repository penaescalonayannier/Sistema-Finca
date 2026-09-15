package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.domain.dto.CategoriaTipoCultivo;
import com.kynsoft.report.infrastructure.entity.TipoCultivo;
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
public interface TipoCultivoReadDataJPARepository extends JpaRepository<TipoCultivo, UUID>, JpaSpecificationExecutor<TipoCultivo> {

    @Override
    Page<TipoCultivo> findAll(Specification<TipoCultivo> specification, Pageable pageable);

    Optional<TipoCultivo> findByCodigo(String codigo);

    List<TipoCultivo> findByActivoTrueOrderByOrdenAsc();

    List<TipoCultivo> findByCategoriaAndActivoTrueOrderByOrdenAsc(CategoriaTipoCultivo categoria);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, UUID id);
}
