package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.PlanIngresoVenta;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface PlanIngresoVentaReadDataJPARepository extends JpaRepository<PlanIngresoVenta, UUID>,
        JpaSpecificationExecutor<PlanIngresoVenta> {
    @Override
    @EntityGraph(attributePaths = {"elementos", "actividades", "actividades.dotaciones", "actividades.gastos"})
    Optional<PlanIngresoVenta> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"elementos", "actividades", "actividades.dotaciones", "actividades.gastos"})
    org.springframework.data.domain.Page<PlanIngresoVenta> findAll(org.springframework.data.jpa.domain.Specification<PlanIngresoVenta> specification,
            org.springframework.data.domain.Pageable pageable);

    @EntityGraph(attributePaths = {"elementos", "actividades", "actividades.dotaciones", "actividades.gastos"})
    Optional<PlanIngresoVenta> findByFincaIdAndPeriodo(UUID fincaId, LocalDate periodo);

    boolean existsByFincaIdAndPeriodo(UUID fincaId, LocalDate periodo);
}
