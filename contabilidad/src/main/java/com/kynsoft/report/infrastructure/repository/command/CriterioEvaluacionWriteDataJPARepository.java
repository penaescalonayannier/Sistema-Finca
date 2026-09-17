package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.CriterioEvaluacion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CriterioEvaluacionWriteDataJPARepository extends JpaRepository<CriterioEvaluacion, UUID> {
}
