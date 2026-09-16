package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.LiquidacionSalida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface LiquidacionSalidaReadDataJPARepository extends JpaRepository<LiquidacionSalida, UUID> {
    List<LiquidacionSalida> findByIdIn(List<UUID> ids);
}
