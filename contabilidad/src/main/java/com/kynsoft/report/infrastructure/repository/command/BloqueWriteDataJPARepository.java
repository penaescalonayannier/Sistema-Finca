package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.Bloque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BloqueWriteDataJPARepository extends JpaRepository<Bloque, UUID> {
}
