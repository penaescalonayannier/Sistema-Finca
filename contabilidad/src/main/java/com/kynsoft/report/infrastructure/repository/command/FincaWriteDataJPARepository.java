package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.Finca;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FincaWriteDataJPARepository extends JpaRepository<Finca, UUID> {
}