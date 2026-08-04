package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrabajadorDiaWriteDataJPARepository extends JpaRepository<TrabajadorDia, UUID> {
}