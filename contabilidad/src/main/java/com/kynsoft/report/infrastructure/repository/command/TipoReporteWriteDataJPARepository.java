package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.TipoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(transactionManager = "writeTransactionManager")
public interface TipoReporteWriteDataJPARepository extends JpaRepository<TipoReporte, UUID> {
}
