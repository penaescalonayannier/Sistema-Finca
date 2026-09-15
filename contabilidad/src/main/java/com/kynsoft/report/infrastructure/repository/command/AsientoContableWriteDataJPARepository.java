package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.AsientoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AsientoContableWriteDataJPARepository extends JpaRepository<AsientoContable, UUID> {

    @Modifying
    @Query("UPDATE AsientoContable a SET a.asentado = true, a.fechaAsentado = :fecha, a.usuarioAsento = :usuario WHERE a.id = :id")
    void asentar(@Param("id") UUID id, @Param("fecha") LocalDateTime fecha, @Param("usuario") String usuario);

    @Modifying
    @Query("UPDATE AsientoContable a SET a.asentado = false, a.fechaAsentado = null, a.usuarioAsento = null WHERE a.id = :id")
    void desasentar(@Param("id") UUID id);

    @Modifying
    @Query("DELETE FROM AsientoContable a WHERE a.movimientoStockId = :movimientoId")
    void deleteByMovimientoStockId(@Param("movimientoId") UUID movimientoId);
}
