package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrabajadorWriteDataJPARepository extends JpaRepository<Trabajador, UUID> {}
