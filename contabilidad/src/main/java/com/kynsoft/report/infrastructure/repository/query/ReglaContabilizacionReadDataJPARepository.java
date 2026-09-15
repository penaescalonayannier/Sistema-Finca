package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.infrastructure.entity.ReglaContabilizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReglaContabilizacionReadDataJPARepository
        extends JpaRepository<ReglaContabilizacion, UUID>, JpaSpecificationExecutor<ReglaContabilizacion> {

    List<ReglaContabilizacion> findByActivoTrue();

    List<ReglaContabilizacion> findByTipoMovimientoAndActivoTrue(TipoMovimientoStock tipoMovimiento);

    @Query("SELECT r FROM ReglaContabilizacion r WHERE r.tipoMovimiento = :tipo " +
           "AND r.activo = true " +
           "AND (r.fincaId = :fincaId OR r.fincaId IS NULL) " +
           "AND (r.almacenId = :almacenId OR r.almacenId IS NULL) " +
           "AND (r.tipoProducto = :tipoProducto OR r.tipoProducto IS NULL) " +
           "ORDER BY r.prioridad ASC")
    List<ReglaContabilizacion> findReglasAplicables(
            @Param("tipo") TipoMovimientoStock tipoMovimiento,
            @Param("fincaId") UUID fincaId,
            @Param("almacenId") UUID almacenId,
            @Param("tipoProducto") String tipoProducto);

    @Query("SELECT r FROM ReglaContabilizacion r WHERE r.tipoMovimiento = :tipo " +
           "AND r.activo = true " +
           "ORDER BY r.prioridad ASC")
    List<ReglaContabilizacion> findByTipoMovimientoOrdenadoPorPrioridad(
            @Param("tipo") TipoMovimientoStock tipoMovimiento);

    Optional<ReglaContabilizacion> findFirstByTipoMovimientoAndActivoTrueOrderByPrioridadAsc(
            TipoMovimientoStock tipoMovimiento);

    List<ReglaContabilizacion> findByFincaIdAndActivoTrue(UUID fincaId);

    List<ReglaContabilizacion> findByAlmacenIdAndActivoTrue(UUID almacenId);

    @Query("SELECT DISTINCT r.tipoMovimiento FROM ReglaContabilizacion r WHERE r.activo = true")
    List<TipoMovimientoStock> findTiposMovimientoConRegla();
}
