package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.EstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstadoCuentaWriteDataJPARepository extends JpaRepository<EstadoCuenta, UUID> {}