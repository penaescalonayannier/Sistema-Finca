package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GrupoWriteDataJPARepository extends JpaRepository<Grupo, UUID> {}
