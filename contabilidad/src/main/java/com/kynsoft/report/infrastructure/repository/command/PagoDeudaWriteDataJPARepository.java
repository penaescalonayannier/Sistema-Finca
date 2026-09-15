package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.PagoDeuda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PagoDeudaWriteDataJPARepository extends JpaRepository<PagoDeuda, UUID> {
}
