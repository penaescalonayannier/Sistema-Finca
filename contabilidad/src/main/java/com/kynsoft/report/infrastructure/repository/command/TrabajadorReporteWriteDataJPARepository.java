package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.TrabajadorReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TrabajadorReporteWriteDataJPARepository extends JpaRepository<TrabajadorReporte, UUID> {
}