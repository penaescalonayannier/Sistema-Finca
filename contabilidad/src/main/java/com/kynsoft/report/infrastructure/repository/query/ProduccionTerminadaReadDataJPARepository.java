package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.ProduccionTerminada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface ProduccionTerminadaReadDataJPARepository 
        extends JpaRepository<ProduccionTerminada, UUID>, JpaSpecificationExecutor<ProduccionTerminada> {

    @Override
    @EntityGraph(attributePaths = {"finca", "producto", "trabajadorEntrega", "trabajadorRecibe"})
    Optional<ProduccionTerminada> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"finca", "producto", "trabajadorEntrega", "trabajadorRecibe"})
    Page<ProduccionTerminada> findAll(Specification specification, Pageable pageable);

    @Query("SELECT pt FROM ProduccionTerminada pt " +
           "JOIN FETCH pt.finca f " +
           "JOIN FETCH pt.producto p " +
           "JOIN FETCH pt.trabajadorEntrega te " +
           "JOIN FETCH pt.trabajadorRecibe tr " +
           "WHERE pt.id = :id AND pt.activo = true")
    Optional<ProduccionTerminada> findByIdWithDetails(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"finca", "producto", "trabajadorEntrega", "trabajadorRecibe"})
    List<ProduccionTerminada> findByProductoIdAndActivoTrue(UUID productoId);

    @EntityGraph(attributePaths = {"finca", "producto", "trabajadorEntrega", "trabajadorRecibe"})
    List<ProduccionTerminada> findByFechaBetweenAndActivoTrue(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    @EntityGraph(attributePaths = {"finca", "producto", "trabajadorEntrega", "trabajadorRecibe"})
    List<ProduccionTerminada> findByTrabajadorEntregaIdAndActivoTrue(UUID trabajadorId);

    @EntityGraph(attributePaths = {"finca", "producto", "trabajadorEntrega", "trabajadorRecibe"})
    List<ProduccionTerminada> findByTrabajadorRecibeIdAndActivoTrue(UUID trabajadorId);

    // Por finca
    @EntityGraph(attributePaths = {"finca", "producto", "trabajadorEntrega", "trabajadorRecibe"})
    List<ProduccionTerminada> findByFincaIdAndActivoTrue(UUID fincaId);

    @EntityGraph(attributePaths = {"finca", "producto", "trabajadorEntrega", "trabajadorRecibe"})
    List<ProduccionTerminada> findByFincaIdAndFechaBetweenAndActivoTrue(
            UUID fincaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Mantener para uso interno (incluye inactivos)
    @EntityGraph(attributePaths = {"finca", "producto", "trabajadorEntrega", "trabajadorRecibe"})
    List<ProduccionTerminada> findByProductoId(UUID productoId);
    @EntityGraph(attributePaths = {"finca", "producto", "trabajadorEntrega", "trabajadorRecibe"})
    List<ProduccionTerminada> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    @EntityGraph(attributePaths = {"finca", "producto", "trabajadorEntrega", "trabajadorRecibe"})
    List<ProduccionTerminada> findByTrabajadorEntregaId(UUID trabajadorId);
    @EntityGraph(attributePaths = {"finca", "producto", "trabajadorEntrega", "trabajadorRecibe"})
    List<ProduccionTerminada> findByTrabajadorRecibeId(UUID trabajadorId);

    @Query("SELECT pt.numeroDocumento FROM ProduccionTerminada pt WHERE pt.fincaId = :fincaId "
            + "AND pt.fecha >= :inicio AND pt.fecha < :fin")
    List<String> findNumerosDocumentoPorFincaYAnio(@Param("fincaId") UUID fincaId,
                                                     @Param("inicio") LocalDateTime inicio,
                                                     @Param("fin") LocalDateTime fin);
}
