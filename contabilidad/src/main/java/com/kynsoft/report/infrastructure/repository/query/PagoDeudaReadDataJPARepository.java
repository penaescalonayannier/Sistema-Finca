package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.PagoDeuda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface PagoDeudaReadDataJPARepository extends JpaRepository<PagoDeuda, UUID>, JpaSpecificationExecutor<PagoDeuda> {
    @Override
    @EntityGraph(attributePaths = {"trabajador", "trabajador.finca", "finca"})
    Page<PagoDeuda> findAll(Specification specification, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"trabajador", "trabajador.finca", "finca"})
    java.util.Optional<PagoDeuda> findById(UUID id);

    @EntityGraph(attributePaths = {"trabajador", "trabajador.finca", "finca"})
    List<PagoDeuda> findByTrabajadorIdOrderByFechaDesc(UUID trabajadorId);

    @Query("SELECT p FROM PagoDeuda p LEFT JOIN FETCH p.trabajador t LEFT JOIN FETCH t.finca LEFT JOIN FETCH p.finca " +
           "WHERE p.fecha >= :startDate AND p.fecha <= :endDate ORDER BY p.fecha DESC")
    List<PagoDeuda> findByFechaBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM PagoDeuda p LEFT JOIN FETCH p.trabajador t LEFT JOIN FETCH t.finca LEFT JOIN FETCH p.finca " +
           "WHERE p.fincaId = :fincaId AND p.fecha >= :startDate AND p.fecha <= :endDate " +
           "ORDER BY p.fecha DESC")
    List<PagoDeuda> findByFincaIdAndFechaBetween(
            @Param("fincaId") UUID fincaId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
