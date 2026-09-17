package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.HistorialSalario;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialSalarioWriteDataJPARepository extends JpaRepository<HistorialSalario, UUID> {
}
