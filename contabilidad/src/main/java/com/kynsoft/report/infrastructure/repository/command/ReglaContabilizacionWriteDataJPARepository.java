package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.domain.dto.TipoMovimientoStock;
import com.kynsoft.report.infrastructure.entity.ReglaContabilizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ReglaContabilizacionWriteDataJPARepository extends JpaRepository<ReglaContabilizacion, UUID> {

    @Modifying
    @Query("UPDATE ReglaContabilizacion r SET r.activo = false WHERE r.id = :id")
    void desactivar(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE ReglaContabilizacion r SET r.activo = true WHERE r.id = :id")
    void activar(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE ReglaContabilizacion r SET r.activo = false WHERE r.tipoMovimiento = :tipo")
    void desactivarPorTipoMovimiento(@Param("tipo") TipoMovimientoStock tipoMovimiento);

    @Modifying
    @Query("UPDATE ReglaContabilizacion r SET r.prioridad = :prioridad WHERE r.id = :id")
    void actualizarPrioridad(@Param("id") UUID id, @Param("prioridad") Integer prioridad);
}
