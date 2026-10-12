package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.PlanIngresoVenta;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanIngresoVentaWriteDataJPARepository extends JpaRepository<PlanIngresoVenta, UUID> {
}
