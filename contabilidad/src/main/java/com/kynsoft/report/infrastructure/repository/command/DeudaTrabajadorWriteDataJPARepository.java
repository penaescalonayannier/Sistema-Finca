package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.DeudaTrabajador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeudaTrabajadorWriteDataJPARepository extends JpaRepository<DeudaTrabajador, UUID> {
}
