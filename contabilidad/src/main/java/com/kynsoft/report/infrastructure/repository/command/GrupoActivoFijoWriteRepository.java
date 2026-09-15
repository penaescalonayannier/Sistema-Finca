package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.GrupoActivoFijo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GrupoActivoFijoWriteRepository extends JpaRepository<GrupoActivoFijo, UUID> {
}
