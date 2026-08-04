package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Evaluacion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluacionReadDataJPARepository extends JpaRepository<Evaluacion, UUID>,
        JpaSpecificationExecutor<Evaluacion> {
}
