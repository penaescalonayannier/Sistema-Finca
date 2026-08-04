package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.HombreActividadAgricolaImporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HombreActividadAgricolaImporteWriteDataJPARepository extends JpaRepository<HombreActividadAgricolaImporte, UUID>{
}
