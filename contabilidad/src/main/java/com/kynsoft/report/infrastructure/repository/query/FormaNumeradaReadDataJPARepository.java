package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.FormaNumerada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface FormaNumeradaReadDataJPARepository extends JpaRepository<FormaNumerada, UUID> {
    Optional<FormaNumerada> findByCodigo(String codigo);
    List<FormaNumerada> findByActivaTrueOrderByNombreAsc();
}
