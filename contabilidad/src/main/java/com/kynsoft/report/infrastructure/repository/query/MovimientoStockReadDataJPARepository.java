package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.infrastructure.entity.MovimientoStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface MovimientoStockReadDataJPARepository extends JpaRepository<MovimientoStock, UUID>, JpaSpecificationExecutor<MovimientoStock> {

    @Query("SELECT m FROM MovimientoStock m WHERE m.fincaProductoId = :fincaProductoId ORDER BY m.fecha DESC")
    List<MovimientoStock> findByFincaProductoId(@Param("fincaProductoId") UUID fincaProductoId);

    @Query("SELECT m FROM MovimientoStock m WHERE m.fincaProductoId = :fincaProductoId ORDER BY m.fecha DESC")
    Page<MovimientoStock> findByFincaProductoId(@Param("fincaProductoId") UUID fincaProductoId, Pageable pageable);

    @Query("SELECT m FROM MovimientoStock m WHERE m.fincaId = :fincaId ORDER BY m.fecha DESC")
    List<MovimientoStock> findByFincaId(@Param("fincaId") UUID fincaId);

    @Query("SELECT m FROM MovimientoStock m WHERE m.productoId = :productoId ORDER BY m.fecha DESC")
    List<MovimientoStock> findByProductoId(@Param("productoId") UUID productoId);

    @Query("SELECT m FROM MovimientoStock m WHERE m.referenciaId = :referenciaId AND m.referenciaTabla = :referenciaTabla ORDER BY m.fecha DESC")
    List<MovimientoStock> findByReferencia(@Param("referenciaId") UUID referenciaId, @Param("referenciaTabla") String referenciaTabla);

    @Query("SELECT m FROM MovimientoStock m WHERE m.tipo = :tipo ORDER BY m.fecha DESC")
    List<MovimientoStock> findByTipo(@Param("tipo") TipoMovimientoStock tipo);

    @Query("SELECT m FROM MovimientoStock m WHERE m.fincaId = :fincaId AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha DESC")
    List<MovimientoStock> findByFincaIdAndFechaBetween(
            @Param("fincaId") UUID fincaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT m FROM MovimientoStock m WHERE m.almacenId = :almacenId ORDER BY m.fecha DESC")
    List<MovimientoStock> findByAlmacenId(@Param("almacenId") UUID almacenId);

    @Query("SELECT m FROM MovimientoStock m WHERE m.almacenId = :almacenId AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha DESC")
    List<MovimientoStock> findByAlmacenIdAndFechaBetween(
            @Param("almacenId") UUID almacenId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT m FROM MovimientoStock m WHERE m.tipo = :tipo AND m.fincaId = :fincaId AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha DESC")
    List<MovimientoStock> findByTipoAndFincaIdAndFechaBetween(
            @Param("tipo") TipoMovimientoStock tipo,
            @Param("fincaId") UUID fincaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT m FROM MovimientoStock m WHERE m.fincaProductoId = :fincaProductoId AND m.almacenId = :almacenId AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha ASC")
    List<MovimientoStock> findByFincaProductoIdAndAlmacenIdAndFechaBetween(
            @Param("fincaProductoId") UUID fincaProductoId,
            @Param("almacenId") UUID almacenId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT m FROM MovimientoStock m WHERE m.fincaProductoId = :fincaProductoId AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha ASC")
    List<MovimientoStock> findByFincaProductoIdAndFechaBetween(
            @Param("fincaProductoId") UUID fincaProductoId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT DISTINCT m.fincaProductoId FROM MovimientoStock m WHERE m.fincaId = :fincaId AND m.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<UUID> findDistinctFincaProductoIdsByFincaIdAndFechaBetween(
            @Param("fincaId") UUID fincaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT m FROM MovimientoStock m WHERE m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha DESC")
    List<MovimientoStock> findByFechaBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}
