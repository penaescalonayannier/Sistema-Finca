package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.TomaPrestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TomaPrestamoWriteDataJPARepository extends JpaRepository<TomaPrestamo, UUID> {}
