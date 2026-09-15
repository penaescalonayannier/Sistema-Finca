package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.AsientoContable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AsientoContableReadDataJPARepository
        extends JpaRepository<AsientoContable, UUID>, JpaSpecificationExecutor<AsientoContable> {

    Optional<AsientoContable> findByNumero(String numero);

    Optional<AsientoContable> findByMovimientoStockId(UUID movimientoStockId);

    List<AsientoContable> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    Page<AsientoContable> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);

    List<AsientoContable> findByAsentadoTrue();

    List<AsientoContable> findByAsentadoFalse();

    @Query("SELECT a FROM AsientoContable a WHERE a.fecha = :fecha ORDER BY a.numero ASC")
    List<AsientoContable> findByFechaOrderByNumero(@Param("fecha") LocalDate fecha);

    @Query("SELECT a FROM AsientoContable a WHERE a.reglaId = :reglaId")
    List<AsientoContable> findByReglaId(@Param("reglaId") UUID reglaId);

    @Query("SELECT a FROM AsientoContable a LEFT JOIN FETCH a.lineas WHERE a.id = :id")
    Optional<AsientoContable> findByIdWithLineas(@Param("id") UUID id);

    @Query("SELECT a FROM AsientoContable a LEFT JOIN FETCH a.lineas WHERE a.movimientoStockId = :movimientoId")
    Optional<AsientoContable> findByMovimientoStockIdWithLineas(@Param("movimientoId") UUID movimientoId);

    @Query("SELECT COUNT(a) FROM AsientoContable a WHERE a.fecha = :fecha")
    Long countByFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT SUM(a.totalDebe) FROM AsientoContable a WHERE a.fecha BETWEEN :inicio AND :fin AND a.asentado = true")
    BigDecimal sumTotalDebePorPeriodo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT SUM(a.totalHaber) FROM AsientoContable a WHERE a.fecha BETWEEN :inicio AND :fin AND a.asentado = true")
    BigDecimal sumTotalHaberPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT a FROM AsientoContable a WHERE a.totalDebe <> a.totalHaber")
    List<AsientoContable> findAsientosDescuadrados();

    @Query("SELECT MAX(a.numero) FROM AsientoContable a WHERE a.numero LIKE :prefijo%")
    String findMaxNumeroByPrefijo(@Param("prefijo") String prefijo);

    boolean existsByMovimientoStockId(UUID movimientoStockId);

    @Query("SELECT DISTINCT a FROM AsientoContable a LEFT JOIN FETCH a.lineas " +
           "WHERE a.movimientoStockId IN (SELECT m.id FROM MovimientoStock m WHERE m.almacenId = :almacenId) " +
           "ORDER BY a.fecha DESC, a.numero DESC")
    List<AsientoContable> findByAlmacenId(@Param("almacenId") UUID almacenId);

    @Query("SELECT DISTINCT a FROM AsientoContable a LEFT JOIN FETCH a.lineas " +
           "WHERE a.movimientoStockId IN (SELECT m.id FROM MovimientoStock m WHERE m.almacenId = :almacenId) " +
           "AND a.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY a.fecha DESC, a.numero DESC")
    List<AsientoContable> findByAlmacenIdAndFechaBetween(
            @Param("almacenId") UUID almacenId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
