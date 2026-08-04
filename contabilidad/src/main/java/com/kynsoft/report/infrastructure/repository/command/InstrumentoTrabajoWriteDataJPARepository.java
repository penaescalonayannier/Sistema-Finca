package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.InstrumentoTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstrumentoTrabajoWriteDataJPARepository extends JpaRepository<InstrumentoTrabajo, UUID> {
}
