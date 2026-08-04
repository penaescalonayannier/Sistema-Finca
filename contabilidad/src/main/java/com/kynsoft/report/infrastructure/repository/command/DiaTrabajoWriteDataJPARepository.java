package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.DiaTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DiaTrabajoWriteDataJPARepository extends JpaRepository<DiaTrabajo, UUID> {
}