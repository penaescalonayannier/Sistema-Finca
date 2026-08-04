package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReporteWriteDataJPARepository extends JpaRepository<Reporte, UUID> {
}