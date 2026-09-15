package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.DeudaTrabajadorDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface DeudaTrabajadorDetalleWriteDataJPARepository extends JpaRepository<DeudaTrabajadorDetalle, UUID> {

    @Modifying
    @Query("UPDATE DeudaTrabajadorDetalle d SET d.activo = false WHERE d.salidaId = :salidaId")
    void desactivarBySalidaId(@Param("salidaId") UUID salidaId);
}
