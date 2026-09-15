package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.LineaAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LineaAsientoReadDataJPARepository
        extends JpaRepository<LineaAsiento, UUID>, JpaSpecificationExecutor<LineaAsiento> {

    List<LineaAsiento> findByAsientoIdOrderByOrdenAsc(UUID asientoId);

    @Query("SELECT l FROM LineaAsiento l WHERE l.codigoCuenta = :codigo ORDER BY l.createdAt DESC")
    List<LineaAsiento> findByCodigoCuenta(@Param("codigo") String codigoCuenta);

    @Query("SELECT l FROM LineaAsiento l WHERE l.codigoCuenta LIKE :prefijo% ORDER BY l.createdAt DESC")
    List<LineaAsiento> findByCodigoCuentaStartingWith(@Param("prefijo") String prefijo);

    @Query("SELECT l FROM LineaAsiento l JOIN l.asiento a WHERE l.codigoCuenta = :codigo " +
           "AND a.fecha BETWEEN :inicio AND :fin AND a.asentado = true ORDER BY a.fecha, a.numero")
    List<LineaAsiento> findByCodigoCuentaAndPeriodo(
            @Param("codigo") String codigoCuenta,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    @Query("SELECT SUM(l.debe) FROM LineaAsiento l JOIN l.asiento a WHERE l.codigoCuenta = :codigo " +
           "AND a.fecha BETWEEN :inicio AND :fin AND a.asentado = true")
    BigDecimal sumDebePorCuentaYPeriodo(
            @Param("codigo") String codigoCuenta,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    @Query("SELECT SUM(l.haber) FROM LineaAsiento l JOIN l.asiento a WHERE l.codigoCuenta = :codigo " +
           "AND a.fecha BETWEEN :inicio AND :fin AND a.asentado = true")
    BigDecimal sumHaberPorCuentaYPeriodo(
            @Param("codigo") String codigoCuenta,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    @Query("SELECT l FROM LineaAsiento l WHERE l.centroCosto = :centroCosto ORDER BY l.createdAt DESC")
    List<LineaAsiento> findByCentroCosto(@Param("centroCosto") String centroCosto);

    @Query("SELECT l FROM LineaAsiento l JOIN l.asiento a WHERE l.centroCosto = :centroCosto " +
           "AND a.fecha BETWEEN :inicio AND :fin AND a.asentado = true ORDER BY a.fecha")
    List<LineaAsiento> findByCentroCostoYPeriodo(
            @Param("centroCosto") String centroCosto,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    @Query("SELECT SUM(l.debe) - SUM(l.haber) FROM LineaAsiento l JOIN l.asiento a " +
           "WHERE l.codigoCuenta = :codigo AND a.asentado = true")
    BigDecimal calcularSaldoCuenta(@Param("codigo") String codigoCuenta);

    @Query("SELECT DISTINCT l.codigoCuenta FROM LineaAsiento l JOIN l.asiento a " +
           "WHERE a.fecha BETWEEN :inicio AND :fin ORDER BY l.codigoCuenta")
    List<String> findCuentasConMovimientoEnPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);
}
