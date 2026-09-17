package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.CriterioEvaluacion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface CriterioEvaluacionReadDataJPARepository extends JpaRepository<CriterioEvaluacion, UUID> {
    List<CriterioEvaluacion> findByActivoTrueOrderByOrdenAscNombreAsc();
    List<CriterioEvaluacion> findAllByOrderByOrdenAscNombreAsc();
}
