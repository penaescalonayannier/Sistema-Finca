package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.domain.dto.EstadoArqueoCaja;
import com.kynsoft.report.infrastructure.entity.ArqueoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface ArqueoCajaReadDataJPARepository extends JpaRepository<ArqueoCaja, UUID> {
    List<ArqueoCaja> findByFincaIdOrderByFechaAperturaDesc(UUID fincaId);
    boolean existsByFincaIdAndEstado(UUID fincaId, EstadoArqueoCaja estado);
}
